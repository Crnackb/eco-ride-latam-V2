package com.ecoride.passenger.web;

import com.ecoride.passenger.domain.DriverProfile;
import com.ecoride.passenger.domain.Passenger;
import com.ecoride.passenger.domain.Rating;
import com.ecoride.passenger.repository.DriverProfileRepository;
import com.ecoride.passenger.repository.PassengerRepository;
import com.ecoride.passenger.repository.RatingRepository;
import com.ecoride.passenger.web.dto.CreateDriverProfileRequest;
import com.ecoride.passenger.web.dto.CreateRatingRequest;
import com.ecoride.passenger.web.dto.DriverProfileDto;
import com.ecoride.passenger.web.dto.PassengerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerRepository passengerRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final RatingRepository ratingRepository;

    @GetMapping("/me")
    public PassengerDto me(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String sub = jwt.getSubject();
        String name = jwt.getClaimAsString("name");
        if (name == null) {
            name = jwt.getClaimAsString("preferred_username");
        }
        String email = jwt.getClaimAsString("email");

        String finalName = name;
        Passenger passenger = passengerRepository.findByKeycloakSub(sub)
                .orElseGet(() -> {
                    Passenger p = new Passenger();
                    p.setKeycloakSub(sub);
                    p.setName(finalName != null ? finalName : "unknown");
                    p.setEmail(email != null ? email : "unknown@example.com");
                    p.setRatingAvg(0.0);
                    p.setCreatedAt(LocalDateTime.now());
                    return passengerRepository.save(p);
                });

        return new PassengerDto(
                passenger.getId(),
                passenger.getName(),
                passenger.getEmail(),
                passenger.getRatingAvg()
        );
    }

    @PostMapping("/drivers/profile")
    @ResponseStatus(HttpStatus.CREATED)
    public DriverProfileDto createOrUpdateDriverProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateDriverProfileRequest request) {

        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String sub = jwt.getSubject();

        Passenger passenger = passengerRepository.findByKeycloakSub(sub)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger not found"));

        DriverProfile profile = driverProfileRepository.findByPassengerId(passenger.getId())
                .orElseGet(DriverProfile::new);

        profile.setPassengerId(passenger.getId());
        profile.setLicenseNo(request.getLicenseNo());
        profile.setCarPlate(request.getCarPlate());
        profile.setSeatsOffered(request.getSeatsOffered());
        if (profile.getVerificationStatus() == null) {
            profile.setVerificationStatus("PENDING");
        }

        profile = driverProfileRepository.save(profile);

        return new DriverProfileDto(
                profile.getId(),
                profile.getLicenseNo(),
                profile.getCarPlate(),
                profile.getSeatsOffered(),
                profile.getVerificationStatus()
        );
    }

    @PostMapping("/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRating(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateRatingRequest request) {

        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String sub = jwt.getSubject();

        Passenger fromPassenger = passengerRepository.findByKeycloakSub(sub)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger not found"));

        Passenger toPassenger = passengerRepository.findById(request.getToPassengerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rated passenger not found"));

        Rating rating = new Rating();
        rating.setTripId(request.getTripId());
        rating.setFromId(fromPassenger.getId());
        rating.setToId(toPassenger.getId());
        rating.setScore(request.getScore());
        rating.setComment(request.getComment());

        ratingRepository.save(rating);

        // Recalcular rating_avg
        List<Rating> ratingsForUser = ratingRepository.findByToId(toPassenger.getId());
        double avg = ratingsForUser.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);

        toPassenger.setRatingAvg(avg);
        passengerRepository.save(toPassenger);
    }
}
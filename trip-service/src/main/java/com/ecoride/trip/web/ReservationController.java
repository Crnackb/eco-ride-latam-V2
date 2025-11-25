package com.ecoride.trip.web;

import com.ecoride.trip.domain.Reservation;
import com.ecoride.trip.domain.ReservationStatus;
import com.ecoride.trip.domain.Trip;
import com.ecoride.trip.messaging.ReservationEventProducer;
import com.ecoride.trip.repository.ReservationRepository;
import com.ecoride.trip.repository.TripRepository;
import com.ecoride.trip.web.dto.CreateReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final TripRepository tripRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationEventProducer reservationEventProducer;

    @PostMapping("/trips/{tripId}/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Reservation createReservation(
            @PathVariable("tripId") Long tripId,
            @RequestBody CreateReservationRequest request
    ) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));

        if (trip.getSeatsAvailable() == null || trip.getSeatsAvailable() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No seats available");
        }

        Reservation reservation = new Reservation();
        reservation.setTripId(tripId);
        reservation.setPassengerId(request.getPassengerId());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);

        // descontamos asiento
        trip.setSeatsAvailable(trip.getSeatsAvailable() - 1);
        if(trip.getSeatsAvailable() <= 0) trip.setStatus("CLOSED");
        tripRepository.save(trip);

        // Saga: disparar evento ReservationRequested
        reservationEventProducer.publishReservationRequested(saved, trip);

        return saved;
    }

    @GetMapping("/reservations/{id}")
    public Reservation getReservation(@PathVariable("id") Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
    }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}
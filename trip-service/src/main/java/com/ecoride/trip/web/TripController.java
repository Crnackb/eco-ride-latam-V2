package com.ecoride.trip.web;

import com.ecoride.trip.domain.Trip;
import com.ecoride.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripRepository tripRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Trip create(@RequestBody Trip trip) {
        // Valores por defecto
        trip.setId(null);
        trip.setSeatsAvailable(trip.getSeatsTotal());
        if (trip.getStatus() == null) {
            trip.setStatus("OPEN");
        }
        return tripRepository.save(trip);
    }

    @GetMapping("/{id}")
    public Trip getById(@PathVariable("id") Long id) {
        return tripRepository.findById(id).orElseThrow();
    }

    @GetMapping
    public List<Trip> getAll() {
        return tripRepository.findAll();
    }
}
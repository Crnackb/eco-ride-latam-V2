package com.ecoride.trip.repository;

import com.ecoride.trip.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByOriginAndDestination(String origin, String destination);

    List<Trip> findByOriginAndDestinationAndStartTimeBetween(
            String origin,
            String destination,
            LocalDateTime from,
            LocalDateTime to
    );
}
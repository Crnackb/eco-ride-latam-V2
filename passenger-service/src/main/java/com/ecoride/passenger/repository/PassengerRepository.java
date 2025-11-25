package com.ecoride.passenger.repository;

import com.ecoride.passenger.domain.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    Optional<Passenger> findByKeycloakSub(String keycloakSub);
}
package com.ecoride.passenger.repository;

import com.ecoride.passenger.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByToId(Long toId);
}
package com.example.Toy_project.repository;

import com.example.Toy_project.entity.PTSubscription;
import com.example.Toy_project.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByReservationTimeBefore(LocalDateTime time);
}


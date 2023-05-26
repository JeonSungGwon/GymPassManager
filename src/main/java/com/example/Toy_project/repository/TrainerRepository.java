package com.example.Toy_project.repository;

import com.example.Toy_project.entity.Reservation;
import com.example.Toy_project.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}

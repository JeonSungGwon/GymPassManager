package com.example.Toy_project.repository;

import com.example.Toy_project.entity.PTSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PTSubscriptionRepository extends JpaRepository<PTSubscription, Long> {
}
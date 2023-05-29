package com.example.Toy_project.repository;

import com.example.Toy_project.entity.GymMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymMembershipRepository extends JpaRepository<GymMembership, Long> {
}

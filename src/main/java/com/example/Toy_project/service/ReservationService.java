//package com.example.Toy_project.service;
//
//import com.example.Toy_project.entity.PTSubscription;
//import com.example.Toy_project.entity.Reservation;
//import com.example.Toy_project.repository.PTSubscriptionRepository;
//import com.example.Toy_project.repository.ReservationRepository;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class ReservationService {
//    private final ReservationRepository reservationRepository;
//    private final PTSubscriptionRepository ptSubscriptionRepository;
//
//    public ReservationService(ReservationRepository reservationRepository, PTSubscriptionRepository ptSubscriptionRepository) {
//        this.reservationRepository = reservationRepository;
//        this.ptSubscriptionRepository = ptSubscriptionRepository;
//    }
//
//    public Reservation createReservation(Long ptSubscriptionId, LocalDateTime reservationTime) {
//        PTSubscription ptSubscription = ptSubscriptionRepository.findById(ptSubscriptionId)
//                .orElseThrow(() -> new IllegalArgumentException("PTSubscription not found with id: " + ptSubscriptionId));
//
//        Reservation reservation = new Reservation();
//        reservation.setReservationTime(reservationTime);
//        reservation.setPtSubscription(ptSubscription);
//        return reservationRepository.save(reservation);
//    }
//
//    public List<Reservation> getReservationsByPTSubscription(Long ptSubscriptionId) {
//        PTSubscription ptSubscription = ptSubscriptionRepository.findById(ptSubscriptionId)
//                .orElseThrow(() -> new IllegalArgumentException("PTSubscription not found with id: " + ptSubscriptionId));
//
//        return reservationRepository.findByPTSubscription(ptSubscription);
//    }
//    public Reservation getReservationById(Long id) {
//        return reservationRepository.findById(id).orElse(null);
//    }
//
//    public void cancelReservation(Long reservationId) {
//        Reservation reservation = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + reservationId));
//
//        reservationRepository.delete(reservation);
//    }
//}

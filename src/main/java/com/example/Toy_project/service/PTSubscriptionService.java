package com.example.Toy_project.service;

import com.example.Toy_project.dto.MemberResponseDto;
import com.example.Toy_project.dto.PTSubscriptionRequestDTO;
import com.example.Toy_project.dto.ReservationRequestDTO;
import com.example.Toy_project.entity.Member;
import com.example.Toy_project.entity.PTSubscription;
import com.example.Toy_project.entity.Reservation;
import com.example.Toy_project.repository.MemberRepository;
import com.example.Toy_project.repository.PTSubscriptionRepository;
import com.example.Toy_project.repository.ReservationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Component
@EnableScheduling
public class PTSubscriptionService {
    private final PTSubscriptionRepository ptSubscriptionRepository;
    private final ReservationRepository reservationRepository;

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    public PTSubscriptionService(PTSubscriptionRepository ptSubscriptionRepository, ReservationRepository reservationRepository, MemberService memberService, MemberRepository memberRepository, ModelMapper modelMapper) {
        this.ptSubscriptionRepository = ptSubscriptionRepository;
        this.reservationRepository = reservationRepository;
        this.memberService = memberService;
        this.memberRepository =memberRepository;
        this.modelMapper = modelMapper;
    }

    public PTSubscriptionRequestDTO createPTSubscription(PTSubscriptionRequestDTO requestDTO) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();

        PTSubscription ptSubscription = modelMapper.map(requestDTO, PTSubscription.class);
        Member member = memberRepository.findById(myInfoBySecurity.getId()).orElseThrow(() -> new EntityNotFoundException("Member not found"));

        ptSubscription.setMember(member);


        PTSubscription savedPtSubscription =ptSubscriptionRepository.save(ptSubscription);
        PTSubscriptionRequestDTO responseDTO = modelMapper.map(savedPtSubscription, PTSubscriptionRequestDTO.class);
        responseDTO.setName(member.getName());
        return responseDTO;
    }

    public List<PTSubscriptionRequestDTO> getAllPTSubscriptions() {
        List<PTSubscription> ptSubscriptions = ptSubscriptionRepository.findAll();
        return ptSubscriptions.stream()
                .map(ptSubscription -> {
                    PTSubscriptionRequestDTO dto = modelMapper.map(ptSubscription,PTSubscriptionRequestDTO.class);
                    dto.setName(ptSubscription.getMember().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public PTSubscription getPTSubscriptionById(Long id) {
        return ptSubscriptionRepository.findById(id).orElse(null);
    }

    public PTSubscription updatePTSubscription(Long id, Integer availableCount, Integer usedCount) {
        PTSubscription ptSubscription = getPTSubscriptionById(id);
        ptSubscription.setAvailableCount(availableCount);
        ptSubscription.setUsedCount(usedCount);
        return ptSubscriptionRepository.save(ptSubscription);
    }

    public void deletePTSubscription(Long id) {
        PTSubscription ptSubscription = getPTSubscriptionById(id);
        ptSubscriptionRepository.delete(ptSubscription);
    }

    // PT 예약 생성
    public ReservationRequestDTO createReservation(ReservationRequestDTO requestDTO) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        requestDTO.setMemberId(myInfoBySecurity.getId());

        Reservation reservation = modelMapper.map(requestDTO, Reservation.class);
        Member member = memberRepository.findById(myInfoBySecurity.getId()).orElseThrow(() -> new EntityNotFoundException("Member not found"));
        reservation.setMember(member);

        Reservation savedReservation =reservationRepository.save(reservation);

        return modelMapper.map(savedReservation, ReservationRequestDTO.class);
    }
    public List<ReservationRequestDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(reservation -> {
                    ReservationRequestDTO dto = modelMapper.map(reservation,ReservationRequestDTO.class);
                    dto.setName(reservation.getMember().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // PT 예약 조회
    //public List<Reservation> getReservationsByPTSubscription(PTSubscription ptSubscription) {
      //  return reservationRepository.findByptSubscription(ptSubscription);
    //}

    // PT 예약 취소
    public void cancelReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    @Scheduled(cron = "0 * * * * *") // 매 시간마다 실행
    public void deductPTSubscriptionAutomatically() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 예약된 PT 이용권 중 현재 시간에 해당하는 것을 차감
        List<Reservation> reservations = reservationRepository.findByReservationTimeBefore(currentDateTime);

        for (Reservation reservation : reservations) {
            Member member = reservation.getMember();

            if (member.getPtSubscription() != null && member.getPtSubscription().getAvailableCount() > 0) {
                PTSubscription ptSubscription = member.getPtSubscription();

                // 사용 가능한 횟수 차감
                int availableCount = ptSubscription.getAvailableCount();
                ptSubscription.setAvailableCount(availableCount - 1);

                // 사용된 횟수 증가
                int usedCount = ptSubscription.getUsedCount();
                ptSubscription.setUsedCount(usedCount + 1);

                // PTSubscription 엔티티 저장
                ptSubscriptionRepository.save(ptSubscription);
            }
            reservationRepository.delete(reservation);
        }
    }
}

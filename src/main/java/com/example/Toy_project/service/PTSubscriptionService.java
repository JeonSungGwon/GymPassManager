package com.example.Toy_project.service;

import com.example.Toy_project.dto.MemberResponseDto;
import com.example.Toy_project.dto.PTSubscriptionRequestDTO;
import com.example.Toy_project.dto.ReservationRequestDTO;
import com.example.Toy_project.entity.*;
import com.example.Toy_project.repository.MemberRepository;
import com.example.Toy_project.repository.PTSubscriptionRepository;
import com.example.Toy_project.repository.ReservationRepository;
import com.example.Toy_project.repository.TrainerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
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
    private final TrainerRepository trainerRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    public PTSubscriptionService(PTSubscriptionRepository ptSubscriptionRepository, ReservationRepository reservationRepository, MemberService memberService,
                                 MemberRepository memberRepository, ModelMapper modelMapper, TrainerRepository trainerRepository) {
        this.ptSubscriptionRepository = ptSubscriptionRepository;
        this.reservationRepository = reservationRepository;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.trainerRepository = trainerRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PTSubscriptionRequestDTO createPTSubscription(PTSubscriptionRequestDTO requestDTO,String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with email: " + memberEmail));
        System.out.println(member.getName());

        PTSubscription ptSubscription = modelMapper.map(requestDTO, PTSubscription.class);
        ptSubscription.setMember(member);

        PTSubscription savedPtSubscription = ptSubscriptionRepository.save(ptSubscription);
        System.out.println(savedPtSubscription.getMember().getName());
        PTSubscriptionRequestDTO responseDTO = modelMapper.map(savedPtSubscription, PTSubscriptionRequestDTO.class);
        responseDTO.setName(member.getName());
        return responseDTO;
    }

    @Transactional
    public List<PTSubscriptionRequestDTO> getAllPTSubscriptions() {
        List<PTSubscription> ptSubscriptions = ptSubscriptionRepository.findAll();
        return ptSubscriptions.stream()
                .map(ptSubscription -> {
                    PTSubscriptionRequestDTO dto = modelMapper.map(ptSubscription, PTSubscriptionRequestDTO.class);
                    dto.setName(ptSubscription.getMember().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PTSubscriptionRequestDTO getPTSubscriptionById(Long id) {
        PTSubscription ptSubscription = ptSubscriptionRepository.findById(id).orElse(null);
        PTSubscriptionRequestDTO dto = modelMapper.map(ptSubscription, PTSubscriptionRequestDTO.class);
        dto.setName(ptSubscription.getMember().getName());
        return dto;
    }

    @Transactional
    public PTSubscriptionRequestDTO updatePTSubscription(Long id, PTSubscriptionRequestDTO requestDTO) {
        PTSubscription ptSubscription = ptSubscriptionRepository.findById(id).orElse(null);
        if (requestDTO.getAvailableCount() != null) {
            ptSubscription.setAvailableCount(requestDTO.getAvailableCount());
        }
        if (requestDTO.getUsedCount() != null) {
            ptSubscription.setUsedCount(requestDTO.getUsedCount());
        }
        requestDTO.setName(ptSubscription.getMember().getName());
        PTSubscription savedPtSubscription = ptSubscriptionRepository.save(ptSubscription);
        return modelMapper.map(savedPtSubscription, PTSubscriptionRequestDTO.class);
    }

    @Transactional
    public void deletePTSubscription(Long id) {
        PTSubscription ptSubscription = ptSubscriptionRepository.findById(id).orElse(null);
        ptSubscriptionRepository.delete(ptSubscription);
    }

    // PT 예약 생성
    public ReservationRequestDTO createReservation(ReservationRequestDTO requestDTO) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        requestDTO.setMemberId(myInfoBySecurity.getId());

        Reservation reservation = modelMapper.map(requestDTO, Reservation.class);
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        System.out.println(requestDTO.getReservationTrainerId());
        Trainer trainer = trainerRepository.findById(requestDTO.getReservationTrainerId()).orElse(null);
        reservation.setMember(member);
        reservation.setTrainer(trainer);
        Reservation savedReservation = reservationRepository.save(reservation);

        return modelMapper.map(savedReservation, ReservationRequestDTO.class);
    }

    @Transactional
    public List<ReservationRequestDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(reservation -> {
                    ReservationRequestDTO dto = modelMapper.map(reservation, ReservationRequestDTO.class);
                    dto.setMemberName(reservation.getMember().getName()); // 멤버 이름 설정
                    dto.setTrainerName(reservation.getTrainer().getName()); // 트레이너 이름 설정
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // PT 예약 취소
    public void cancelReservation() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 현재 사용자의 정보를 기반으로 현재 시간 이후의 예약 정보를 가져옵니다.
        List<Reservation> reservations = reservationRepository.findByMemberAndReservationTimeAfter(member, currentDateTime);


        for (Reservation reservation : reservations) {
            reservationRepository.delete(reservation);
        }

    }

    @Transactional
    public ReservationRequestDTO getReservationByMe() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Reservation reservation = member.getReservations().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found for the member"));

        ReservationRequestDTO reservationRequestDTO = modelMapper.map(reservation, ReservationRequestDTO.class);
        reservationRequestDTO.setMemberName(member.getName()); // 멤버 이름 설정
        reservationRequestDTO.setTrainerName(reservation.getTrainer().getName()); // 트레이너 이름 설정

        return reservationRequestDTO;
    }

    @Scheduled(cron = "0 * * * * *") // 매 시간마다 실행
    public void deductPTSubscriptionAutomatically() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 예약된 PT 이용권 중 현재 시간에 해당하는 것을 차감
        List<Reservation> reservations = reservationRepository.findByReservationTimeBefore(currentDateTime);

        for (Reservation reservation : reservations) {
            Member member = reservation.getMember();
            if (member.getPtSubscription() != null && member.getPtSubscription().getAvailableCount() > 0 && !reservation.isExpired()) {
                PTSubscription ptSubscription = member.getPtSubscription();

                // 사용 가능한 횟수 차감
                int availableCount = ptSubscription.getAvailableCount();
                ptSubscription.setAvailableCount(availableCount - 1);

                // 사용된 횟수 증가
                int usedCount = ptSubscription.getUsedCount();
                ptSubscription.setUsedCount(usedCount + 1);
                reservation.setExpired(true);
                // PTSubscription 엔티티 저장
                ptSubscriptionRepository.save(ptSubscription);
            }
        }
    }
}

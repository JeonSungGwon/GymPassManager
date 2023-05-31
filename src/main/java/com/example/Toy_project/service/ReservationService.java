package com.example.Toy_project.service;

import com.example.Toy_project.dto.MemberResponseDto;
import com.example.Toy_project.dto.PTSubscriptionRequestDTO;
import com.example.Toy_project.dto.ReservationRequestDTO;
import com.example.Toy_project.entity.Member;
import com.example.Toy_project.entity.PTSubscription;
import com.example.Toy_project.entity.Reservation;
import com.example.Toy_project.entity.Trainer;
import com.example.Toy_project.repository.MemberRepository;
import com.example.Toy_project.repository.PTSubscriptionRepository;
import com.example.Toy_project.repository.ReservationRepository;
import com.example.Toy_project.repository.TrainerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PTSubscriptionRepository ptSubscriptionRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    public ReservationService(ReservationRepository reservationRepository, PTSubscriptionRepository ptSubscriptionRepository, MemberService memberService
                              ,ModelMapper modelMapper,MemberRepository memberRepository, TrainerRepository trainerRepository){
        this.reservationRepository = reservationRepository;
        this.ptSubscriptionRepository = ptSubscriptionRepository;
        this.memberService = memberService;
        this.modelMapper = modelMapper;
        this.memberRepository = memberRepository;
        this.trainerRepository = trainerRepository;
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
                    dto.setMemberEmail(reservation.getMember().getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationRequestDTO updateReservation(Long id, ReservationRequestDTO requestDTO){
        Reservation reservation = reservationRepository.findById(id).orElse(null);

        if (requestDTO.getReservationTrainerId() != null) {
            reservation.setTrainer(trainerRepository.findById(requestDTO.getReservationTrainerId()).orElse(null));
        }
        if (requestDTO.getReservationTime() != null) {
            reservation.setReservationTime(requestDTO.getReservationTime());
        }
        requestDTO.setMemberName(reservation.getMember().getName());
        requestDTO.setTrainerName(reservation.getTrainer().getName());
        requestDTO.setMemberEmail(reservation.getMember().getEmail());
        Reservation savedReservation = reservationRepository.save(reservation);
        return modelMapper.map(savedReservation,ReservationRequestDTO.class);
    }


    @Transactional
    public ReservationRequestDTO updateMyReservation(ReservationRequestDTO requestDTO){
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        requestDTO.setMemberId(myInfoBySecurity.getId());
        Reservation reservation = reservationRepository.findById(myInfoBySecurity.getId()).orElse(null);
        if (requestDTO.getReservationTrainerId() != null) {
            reservation.setTrainer(trainerRepository.findById(requestDTO.getReservationTrainerId()).orElse(null));
        }
        if (requestDTO.getReservationTime() != null) {
            reservation.setReservationTime(requestDTO.getReservationTime());
        }
        requestDTO.setMemberName(reservation.getMember().getName());
        requestDTO.setTrainerName(reservation.getTrainer().getName());
        requestDTO.setMemberEmail(reservation.getMember().getEmail());
        Reservation savedReservation = reservationRepository.save(reservation);
        return modelMapper.map(savedReservation,ReservationRequestDTO.class);
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
    public List<ReservationRequestDTO> getReservationsByMe() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<Reservation> reservations = member.getReservations();

        if (reservations.isEmpty()) {
            throw new EntityNotFoundException("Reservations not found for the member");
        }

        return reservations.stream()
                .map(reservation -> {
                    ReservationRequestDTO reservationRequestDTO = modelMapper.map(reservation, ReservationRequestDTO.class);
                    reservationRequestDTO.setMemberName(member.getName());
                    reservationRequestDTO.setTrainerName(reservation.getTrainer().getName());
                    reservationRequestDTO.setMemberEmail(reservation.getMember().getEmail());
                    return reservationRequestDTO;
                })
                .collect(Collectors.toList());
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

                reservationRepository.save(reservation);
                // PTSubscription 엔티티 저장
                ptSubscriptionRepository.save(ptSubscription);
            }
        }
    }
}
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
    public PTSubscriptionRequestDTO getPTSubscriptionByMe() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        PTSubscription ptSubscription = member.getPtSubscription();
        PTSubscriptionRequestDTO dto = modelMapper.map(ptSubscription, PTSubscriptionRequestDTO.class);
        dto.setName(ptSubscription.getMember().getName());
        return dto;
    }

    @Transactional
    public PTSubscriptionRequestDTO updatePTSubscription(PTSubscriptionRequestDTO requestDTO,String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail).orElse(null);
        PTSubscription ptSubscription = member.getPtSubscription();
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


}

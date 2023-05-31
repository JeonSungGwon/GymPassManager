package com.example.Toy_project.service;

import com.example.Toy_project.dto.GymMembershipDTO;
import com.example.Toy_project.dto.MemberResponseDto;
import com.example.Toy_project.dto.PTSubscriptionRequestDTO;
import com.example.Toy_project.entity.GymMembership;
import com.example.Toy_project.entity.Member;
import com.example.Toy_project.entity.PTSubscription;
import com.example.Toy_project.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GymMembershipService {
    private final GymMembershipRepository gymMembershipRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;
    public GymMembershipService (GymMembershipRepository gymMembershipRepository, MemberRepository memberRepository,
                                 MemberService memberService, ModelMapper modelMapper){
        this.gymMembershipRepository = gymMembershipRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public GymMembershipDTO createGymMembershipService(GymMembershipDTO requestDTO, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with email: " + memberEmail));

        GymMembership gymMembership = modelMapper.map(requestDTO, GymMembership.class);
        gymMembership.setMember(member);
        GymMembership savedGymMembership = gymMembershipRepository.save(gymMembership);
        GymMembershipDTO responseDTO = modelMapper.map(savedGymMembership, GymMembershipDTO.class);
        responseDTO.setName(member.getName());
        return responseDTO;
    }

    @Transactional
    public List<GymMembershipDTO> getAllGymMemberships() {
        List<GymMembership> gymMemberships = gymMembershipRepository.findAll();
        return gymMemberships.stream()
                .map(gymMembership -> {
                    GymMembershipDTO dto = modelMapper.map(gymMembership, GymMembershipDTO.class);
                    dto.setName(gymMembership.getMember().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public GymMembershipDTO getGymMembershipByMe() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        GymMembership gymMembership = member.getGymMembership();
        GymMembershipDTO dto = modelMapper.map(gymMembership, GymMembershipDTO.class);
        dto.setName(gymMembership.getMember().getName());
        return dto;
    }

    @Transactional
    public GymMembershipDTO updateGymMembership(Long id, GymMembershipDTO requestDTO) {
        GymMembership gymMembership = gymMembershipRepository.findById(id).orElse(null);
        if (requestDTO.getStartDate()!= null) {
            System.out.println(requestDTO.getStartDate());
            gymMembership.setStartDate(requestDTO.getStartDate());
        }
        if (requestDTO.getEndDate()!= null) {
            gymMembership.setEndDate(requestDTO.getEndDate());
        }
        requestDTO.setName(gymMembership.getMember().getName());
        GymMembership savedGymMembership = gymMembershipRepository.save(gymMembership);
        return modelMapper.map(savedGymMembership, GymMembershipDTO.class);
    }

    @Transactional
    public void deleteGymMembership(Long id) {
        GymMembership gymMembership = gymMembershipRepository.findById(id).orElse(null);
        gymMembershipRepository.delete(gymMembership);
    }
}

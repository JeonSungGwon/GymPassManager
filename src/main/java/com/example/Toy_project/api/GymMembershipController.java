package com.example.Toy_project.api;

import com.example.Toy_project.dto.GymMembershipDTO;
import com.example.Toy_project.dto.PTSubscriptionRequestDTO;
import com.example.Toy_project.repository.GymMembershipRepository;
import com.example.Toy_project.repository.MemberRepository;
import com.example.Toy_project.repository.PTSubscriptionRepository;
import com.example.Toy_project.service.GymMembershipService;
import com.example.Toy_project.service.MemberService;
import com.example.Toy_project.service.PTSubscriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GymMembershipController {
        private final GymMembershipService gymMembershipService;
        private final MemberService memberService;
        private  final MemberRepository memberRepository;
        private final GymMembershipRepository gymMembershipRepository;

        public GymMembershipController(GymMembershipService gymMembershipService, MemberService memberService,
                                       MemberRepository memberRepository, GymMembershipRepository gymMembershipRepository) {
            this.gymMembershipService = gymMembershipService;
            this.memberService = memberService;
            this.memberRepository = memberRepository;
            this.gymMembershipRepository = gymMembershipRepository;
        }

        @PostMapping("/admin/GymMembership")
        public GymMembershipDTO createGymMembership(@RequestBody GymMembershipDTO requestDTO, @RequestParam String memberEmail) {
            // 요청 바디에서 필요한 정보를 추출하고 PTSubscription 생성
            return gymMembershipService.createGymMembershipService(requestDTO,memberEmail);
        }

        @GetMapping("/admin/GymMemberships")
        public List<GymMembershipDTO> getAllGymMemberships() {
            return gymMembershipService.getAllGymMemberships();
        }

        @GetMapping("/GymMembership/user")
        public GymMembershipDTO getGymMembershipByMe() {
            return gymMembershipService.getGymMembershipByMe();
        }

        @PatchMapping("/admin/GymMembership/{id}")
        public GymMembershipDTO updateGymMembership(@PathVariable Long id, @RequestBody GymMembershipDTO requestDTO) {
            return gymMembershipService.updateGymMembership(id, requestDTO);
        }

        @DeleteMapping("/admin/GymMembership/{id}")
        public void deleteGymMembership(@PathVariable Long id) {
            gymMembershipService.deleteGymMembership(id);
        }
    }

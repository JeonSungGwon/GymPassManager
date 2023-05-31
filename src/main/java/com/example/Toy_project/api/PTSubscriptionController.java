package com.example.Toy_project.api;

import com.example.Toy_project.dto.MemberResponseDto;
import com.example.Toy_project.dto.PTSubscriptionRequestDTO;
import com.example.Toy_project.entity.Member;
import com.example.Toy_project.entity.PTSubscription;
import com.example.Toy_project.repository.MemberRepository;
import com.example.Toy_project.repository.PTSubscriptionRepository;
import com.example.Toy_project.service.MemberService;
import com.example.Toy_project.service.PTSubscriptionService;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PTSubscriptionController {
    private final PTSubscriptionService ptSubscriptionService;
    private final MemberService memberService;
    private  final MemberRepository memberRepository;
    private final PTSubscriptionRepository ptSubscriptionRepository;

    public PTSubscriptionController(PTSubscriptionService ptSubscriptionService, MemberService memberService, MemberRepository memberRepository, PTSubscriptionRepository ptSubscriptionRepository) {
        this.ptSubscriptionService = ptSubscriptionService;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.ptSubscriptionRepository = ptSubscriptionRepository;
    }

    @PostMapping("/admin/pt-subscriptions")
    public PTSubscriptionRequestDTO createPTSubscription(@RequestBody PTSubscriptionRequestDTO requestDTO, @RequestParam String memberEmail) {
        // 요청 바디에서 필요한 정보를 추출하고 PTSubscription 생성
        return ptSubscriptionService.createPTSubscription(requestDTO,memberEmail);
    }

    @GetMapping("/admin/pt-subscriptions")
    public List<PTSubscriptionRequestDTO> getAllPTSubscriptions() {
        return ptSubscriptionService.getAllPTSubscriptions();
    }

    @GetMapping("/pt-subscriptions/user")
    public PTSubscriptionRequestDTO getPTSubscriptionByMe() {
        return ptSubscriptionService.getPTSubscriptionByMe();
    }

    @PatchMapping("/admin/pt-subscriptions")
    public PTSubscriptionRequestDTO updatePTSubscription(@RequestBody PTSubscriptionRequestDTO requestDTO,@RequestParam String memberEmail) {
        return ptSubscriptionService.updatePTSubscription(requestDTO, memberEmail);
    }

    @DeleteMapping("/admin/pt-subscriptions/{id}")
    public void deletePTSubscription(@PathVariable Long id) {
        ptSubscriptionService.deletePTSubscription(id);
    }
}
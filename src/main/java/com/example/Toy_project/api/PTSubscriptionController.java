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
@RequestMapping("/api/pt-subscriptions")
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

    @PostMapping
    public PTSubscriptionRequestDTO createPTSubscription(@RequestBody PTSubscriptionRequestDTO requestDTO) {
        // 요청 바디에서 필요한 정보를 추출하고 PTSubscription 생성
        return ptSubscriptionService.createPTSubscription(requestDTO);
    }

    @GetMapping
    public List<PTSubscriptionRequestDTO> getAllPTSubscriptions() {
        return ptSubscriptionService.getAllPTSubscriptions();
    }

    @GetMapping("/{id}")
    public PTSubscription getPTSubscriptionById(@PathVariable Long id) {
        return ptSubscriptionService.getPTSubscriptionById(id);
    }

    @PutMapping("/{id}")
    public PTSubscription updatePTSubscription(@PathVariable Long id, @RequestBody PTSubscriptionRequestDTO requestDTO) {
        // 요청 바디에서 필요한 정보를 추출하고 PTSubscription 업데이트
        Integer availableCount = requestDTO.getAvailableCount();
        Integer usedCount = requestDTO.getUsedCount();
        return ptSubscriptionService.updatePTSubscription(id, availableCount, usedCount);
    }

    @DeleteMapping("/{id}")
    public void deletePTSubscription(@PathVariable Long id) {
        ptSubscriptionService.deletePTSubscription(id);
    }
}
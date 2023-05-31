package com.example.Toy_project.service;

import com.example.Toy_project.config.SecurityUtil;
import com.example.Toy_project.dto.MemberResponseDto;
import com.example.Toy_project.dto.TrainerDto;
import com.example.Toy_project.entity.Member;
import com.example.Toy_project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public MemberResponseDto getMyInfoBySecurity() {
        return memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
    }

    public List<MemberResponseDto> getAllMembers() {
        List<Member> members = memberRepository.findAll();

        if (members.isEmpty()) {
            throw new RuntimeException("사용자 정보가 없습니다");
        }

        return members.stream()
                .map(MemberResponseDto::of)
                .collect(Collectors.toList());
    }



    @Transactional
    public MemberResponseDto changeMemberNickname(String email, String nickname) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
        member.setNickname(nickname);
        return MemberResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public MemberResponseDto changeMemberPassword(String exPassword, String newPassword) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
        if (!passwordEncoder.matches(exPassword, member.getPassword())) {
            throw new RuntimeException("비밀번호가 맞지 않습니다");
        }
        member.setPassword(passwordEncoder.encode((newPassword)));
        return MemberResponseDto.of(memberRepository.save(member));
    }
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));
    }

    public List<MemberResponseDto> getMembers(){
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(member -> modelMapper.map(member, MemberResponseDto.class))
                .collect(Collectors.toList());
    }
}
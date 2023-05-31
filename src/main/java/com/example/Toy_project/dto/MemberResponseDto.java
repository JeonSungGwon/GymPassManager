package com.example.Toy_project.dto;

import com.example.Toy_project.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {

    private Long id;
    private String email;
    private String name;
    private String gender;
    private LocalDate gymMembershipStart;
    private LocalDate gymMembershipEnd;
    private int ptSubscription;

    public static MemberResponseDto of(Member member) {
        LocalDate gymMembershipStartDate = member.getGymMembership() != null ? member.getGymMembership().getStartDate() : LocalDate.of(0, 1, 1);
        LocalDate gymMembershipEndDate = member.getGymMembership() != null ? member.getGymMembership().getEndDate() : LocalDate.of(0, 1, 1);
        int ptSubscriptionCount = member.getPtSubscription() != null ? member.getPtSubscription().getAvailableCount() : 0;
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .gender(member.getGender())
                .id(member.getId())
                .gymMembershipStart(gymMembershipStartDate)
                .gymMembershipEnd(gymMembershipEndDate)
                .ptSubscription(ptSubscriptionCount)
                .build();
    }
}
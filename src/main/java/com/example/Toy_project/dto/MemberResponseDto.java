package com.example.Toy_project.dto;

import com.example.Toy_project.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {

    private Long id;
    private String email;
    private String name;
    private String gender;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .gender(member.getGender())
                .id(member.getId())
                .build();
    }
}
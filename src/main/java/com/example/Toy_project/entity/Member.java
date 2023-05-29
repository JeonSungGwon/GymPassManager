package com.example.Toy_project.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Pattern(regexp = "^(Male|Female)$", message = "성별은 Male 또는 Female이어야 합니다.")
    private String gender;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member")
    private PTSubscription ptSubscription;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member")
    private GymMembership gymMembership;

    @OneToMany(mappedBy = "member")
    private List<Reservation> reservations;



    public Member(Long memberId) {
        this.id = memberId;
    }


    public void setNickname(String nickname) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Builder
    public Member(Long id, String email, String password, String name, String gender, Authority authority) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.authority = authority;
    }
}
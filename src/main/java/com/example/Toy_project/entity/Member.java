package com.example.Toy_project.entity;

import lombok.*;

import javax.persistence.*;

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

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToOne(mappedBy = "member")
    private PTSubscription ptSubscription;


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
    public Member(Long id, String email, String password, String name, Authority authority) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.authority = authority;
    }
}
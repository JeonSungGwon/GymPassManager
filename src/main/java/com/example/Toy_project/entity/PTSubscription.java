package com.example.Toy_project.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PTSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int availableCount; // 사용 가능한 횟수
    private int usedCount; // 사용된 횟수

    @OneToOne(mappedBy = "ptSubscription")
    private Member member;
}


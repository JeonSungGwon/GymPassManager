package com.example.Toy_project.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Pattern(regexp = "^(Male|Female)$", message = "성별은 Male 또는 Female이어야 합니다.")
    private String gender;

    @OneToMany(mappedBy = "trainer")
    private List<Reservation> reservations;


}

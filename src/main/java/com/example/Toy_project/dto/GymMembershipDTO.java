package com.example.Toy_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GymMembershipDTO {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

}

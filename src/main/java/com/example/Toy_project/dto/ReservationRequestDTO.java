package com.example.Toy_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ReservationRequestDTO {
    private Long memberId;
    private String name;
    private LocalDateTime reservationTime;
}

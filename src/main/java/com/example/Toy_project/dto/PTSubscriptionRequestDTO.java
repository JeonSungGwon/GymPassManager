package com.example.Toy_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PTSubscriptionRequestDTO {
    private Integer availableCount;
    private String name;
    private Integer usedCount;
}

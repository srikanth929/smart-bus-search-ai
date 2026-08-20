package com.example.busai.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusSearchRequest {

    @NotBlank(message = "source is required")
    private String source;

    @NotBlank(message = "destination is required")
    private String destination;

    @NotNull(message = "date is required")
    @FutureOrPresent(message = "date cannot be in the past")
    private LocalDate date;
}

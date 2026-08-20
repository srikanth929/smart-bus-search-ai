package com.example.busai.dto;

import com.example.busai.entity.BusType;
import com.example.busai.entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Internal representation of a search request, built either from the
 * plain REST query params or extracted by the AI from natural language.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusSearchCriteria {
    private String source;
    private String destination;
    private LocalDate date;
    private SeatType seatType;      // optional
    private BigDecimal maxPrice;    // optional
    private BusType busType;        // optional
    private LocalTime departureAfter; // optional
    private LocalTime departureBefore; // optional
}

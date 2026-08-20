package com.example.busai.dto;

import com.example.busai.entity.BusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusResponse {
    private Long id;
    private String busName;
    private String operatorName;
    private String source;
    private String destination;
    private LocalDate travelDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private BusType busType;
    private BigDecimal price;
    private Integer totalSeats;

    /**
     * Populated only when a specific seat-type filter (e.g. WINDOW) was requested.
     * Holds the matching available seats found for that bus.
     */
    private List<SeatResponse> matchingSeats;
}

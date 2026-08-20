package com.example.busai.controller;

import com.example.busai.dto.BusResponse;
import com.example.busai.dto.BusSearchCriteria;
import com.example.busai.entity.BusType;
import com.example.busai.entity.SeatType;
import com.example.busai.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "Bus Search", description = "Plain and preference-based bus search APIs")
@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @Operation(summary = "Search buses by route and date")
    @GetMapping("/search")
    public ResponseEntity<List<BusResponse>> search(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(busService.searchBuses(source, destination, date));
    }

    @Operation(summary = "Search buses that satisfy seat/price/type/time preferences")
    @GetMapping("/search-with-seats")
    public ResponseEntity<List<BusResponse>> searchWithSeats(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) SeatType seatType,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BusType busType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime departureAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime departureBefore
    ) {
        BusSearchCriteria criteria = BusSearchCriteria.builder()
                .source(source)
                .destination(destination)
                .date(date)
                .seatType(seatType)
                .maxPrice(maxPrice)
                .busType(busType)
                .departureAfter(departureAfter)
                .departureBefore(departureBefore)
                .build();

        return ResponseEntity.ok(busService.searchBusesWithCriteria(criteria));
    }
}

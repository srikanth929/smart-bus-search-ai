package com.example.busai.controller;

import com.example.busai.dto.SeatResponse;
import com.example.busai.entity.SeatType;
import com.example.busai.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Seats", description = "Seat listing and availability APIs")
@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @Operation(summary = "Get all seats for a bus")
    @GetMapping("/{busId}/seats")
    public ResponseEntity<List<SeatResponse>> getAllSeats(@PathVariable Long busId) {
        return ResponseEntity.ok(seatService.getAllSeats(busId));
    }

    @Operation(summary = "Get available seats of a given type for a bus")
    @GetMapping("/{busId}/available-seats")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(
            @PathVariable Long busId,
            @RequestParam SeatType seatType
    ) {
        return ResponseEntity.ok(seatService.getAvailableSeatsByType(busId, seatType));
    }
}

package com.example.busai.ai;

import com.example.busai.dto.SeatResponse;
import com.example.busai.entity.SeatType;
import com.example.busai.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Tool the AI can call to check real seat availability for a specific bus,
 * e.g. after the user has picked one bus from a prior search result.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeatSearchTool {

    private final SeatService seatService;

    @Tool(description = "Find available seats of a given type (WINDOW, AISLE, or MIDDLE) on a specific bus, " +
            "identified by its busId. Use this when the user asks about seat availability on a bus they already " +
            "picked, or to double check availability before confirming results. Never invent seat availability - " +
            "only relay what this tool returns.")
    public List<SeatResponse> findAvailableSeats(
            @ToolParam(description = "The id of the bus to check, as returned by a prior bus search") Long busId,
            @ToolParam(description = "Seat type to filter by: WINDOW, AISLE, or MIDDLE") SeatType seatType
    ) {
        log.info("[AI TOOL] findAvailableSeats busId={} seatType={}", busId, seatType);
        return seatService.getAvailableSeatsByType(busId, seatType);
    }
}

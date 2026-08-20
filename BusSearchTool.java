package com.example.busai.ai;

import com.example.busai.dto.BusResponse;
import com.example.busai.dto.BusSearchCriteria;
import com.example.busai.entity.BusType;
import com.example.busai.entity.SeatType;
import com.example.busai.service.BusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Tools the AI can call to search for real buses. The AI must NEVER
 * fabricate results - it only relays what these tools return, which in
 * turn come straight from the database via BusService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BusSearchTool {

    private final BusService busService;

    @Tool(description = "Search all buses running between a source and destination city on a given travel date, " +
            "with no seat/price/type filtering. Use this only for a broad listing; prefer " +
            "searchBusesWithPreferences when the user has any preference such as seat type, budget, bus type, " +
            "or departure time.")
    public List<BusResponse> searchBuses(
            @ToolParam(description = "Departure city, e.g. Ongole") String source,
            @ToolParam(description = "Arrival city, e.g. Chennai") String destination,
            @ToolParam(description = "Travel date in ISO format yyyy-MM-dd") LocalDate date
    ) {
        log.info("[AI TOOL] searchBuses source={} destination={} date={}", source, destination, date);
        return busService.searchBuses(source, destination, date);
    }

    @Tool(description = "Search buses between a source and destination for a given travel date and return only " +
            "buses that satisfy the user's stated preferences: seat type (WINDOW, AISLE, MIDDLE), maximum price, " +
            "bus type (AC_SLEEPER, NON_AC_SLEEPER, AC_SEATER, NON_AC_SEATER), and/or a departure time window. " +
            "Any preference the user did not mention should be passed as null. This is the preferred tool " +
            "whenever the user mentions any filter at all. Never invent bus, price, or seat data - only relay " +
            "what this tool returns.")
    public List<BusResponse> searchBusesWithPreferences(
            @ToolParam(description = "Departure city, e.g. Ongole") String source,
            @ToolParam(description = "Arrival city, e.g. Chennai") String destination,
            @ToolParam(description = "Travel date in ISO format yyyy-MM-dd. Resolve relative dates " +
                    "like 'tomorrow' or 'today' against the current date before calling this tool.") LocalDate date,
            @ToolParam(required = false, description = "Preferred seat type: WINDOW, AISLE, or MIDDLE. Null if not specified.")
            SeatType seatType,
            @ToolParam(required = false, description = "Maximum ticket price in INR. Null if not specified.")
            BigDecimal maxPrice,
            @ToolParam(required = false, description = "Bus type: AC_SLEEPER, NON_AC_SLEEPER, AC_SEATER, or NON_AC_SEATER. Null if not specified.")
            BusType busType,
            @ToolParam(required = false, description = "Only include buses departing at or after this time (HH:mm). Null if not specified.")
            LocalTime departureAfter,
            @ToolParam(required = false, description = "Only include buses departing at or before this time (HH:mm). Null if not specified.")
            LocalTime departureBefore
    ) {
        log.info("[AI TOOL] searchBusesWithPreferences source={} destination={} date={} seatType={} maxPrice={} " +
                        "busType={} departureAfter={} departureBefore={}",
                source, destination, date, seatType, maxPrice, busType, departureAfter, departureBefore);

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

        return busService.searchBusesWithCriteria(criteria);
    }
}

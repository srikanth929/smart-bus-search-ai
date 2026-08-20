package com.example.busai.controller;

import com.example.busai.dto.BusResponse;
import com.example.busai.entity.BusType;
import com.example.busai.service.BusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BusController.class)
class BusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusService busService;

    @Test
    void searchReturnsBuses() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);
        BusResponse response = BusResponse.builder()
                .id(1L).busName("Orange Travels").operatorName("Orange Travels Pvt Ltd")
                .source("Ongole").destination("Chennai").travelDate(date)
                .departureTime(LocalTime.of(20, 30)).arrivalTime(LocalTime.of(5, 30))
                .busType(BusType.AC_SLEEPER).price(new BigDecimal("850")).totalSeats(36)
                .build();

        when(busService.searchBuses("Ongole", "Chennai", date)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/buses/search")
                        .param("source", "Ongole")
                        .param("destination", "Chennai")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].busName").value("Orange Travels"))
                .andExpect(jsonPath("$[0].price").value(850));
    }

    @Test
    void searchWithSeatsReturnsFilteredBuses() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);
        when(busService.searchBusesWithCriteria(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/buses/search-with-seats")
                        .param("source", "Ongole")
                        .param("destination", "Chennai")
                        .param("date", date.toString())
                        .param("seatType", "WINDOW")
                        .param("maxPrice", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

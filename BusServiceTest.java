package com.example.busai.service;

import com.example.busai.exception.BusSearchException;
import com.example.busai.repository.BusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private BusService busService;

    @Test
    void rejectsPastTravelDate() {
        assertThatThrownBy(() ->
                busService.searchBuses("Ongole", "Chennai", LocalDate.now().minusDays(1)))
                .isInstanceOf(BusSearchException.class)
                .hasMessageContaining("past");
    }

    @Test
    void rejectsSameSourceAndDestination() {
        assertThatThrownBy(() ->
                busService.searchBuses("Chennai", "Chennai", LocalDate.now().plusDays(1)))
                .isInstanceOf(BusSearchException.class)
                .hasMessageContaining("cannot be the same");
    }

    @Test
    void rejectsBlankSource() {
        assertThatThrownBy(() ->
                busService.searchBuses("", "Chennai", LocalDate.now().plusDays(1)))
                .isInstanceOf(BusSearchException.class);
    }
}

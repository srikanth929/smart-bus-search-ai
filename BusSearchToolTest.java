package com.example.busai.ai;

import com.example.busai.dto.BusResponse;
import com.example.busai.entity.SeatType;
import com.example.busai.service.BusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusSearchToolTest {

    @Mock
    private BusService busService;

    @Test
    void searchBusesWithPreferences_delegatesToBusServiceAndReturnsOnlyRealResults() {
        BusSearchTool tool = new BusSearchTool(busService);
        LocalDate date = LocalDate.now().plusDays(1);

        BusResponse busResponse = BusResponse.builder().id(1L).busName("VRL Travels").build();
        when(busService.searchBusesWithCriteria(any())).thenReturn(List.of(busResponse));

        List<BusResponse> result = tool.searchBusesWithPreferences(
                "Ongole", "Chennai", date, SeatType.WINDOW, new BigDecimal("1000"), null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBusName()).isEqualTo("VRL Travels");

        ArgumentCaptor<com.example.busai.dto.BusSearchCriteria> captor =
                ArgumentCaptor.forClass(com.example.busai.dto.BusSearchCriteria.class);
        verify(busService).searchBusesWithCriteria(captor.capture());
        assertThat(captor.getValue().getSeatType()).isEqualTo(SeatType.WINDOW);
        assertThat(captor.getValue().getSource()).isEqualTo("Ongole");
    }

    @Test
    void searchBusesWithPreferences_returnsEmptyListWhenNoMatches_neverFabricatesData() {
        BusSearchTool tool = new BusSearchTool(busService);
        LocalDate date = LocalDate.now().plusDays(1);

        when(busService.searchBusesWithCriteria(any())).thenReturn(List.of());

        List<BusResponse> result = tool.searchBusesWithPreferences(
                "Ongole", "Chennai", date, SeatType.WINDOW, null, null, null, null);

        assertThat(result).isEmpty();
    }
}

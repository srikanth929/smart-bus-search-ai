package com.example.busai.repository;

import com.example.busai.entity.Bus;
import com.example.busai.entity.BusType;
import com.example.busai.entity.Seat;
import com.example.busai.entity.SeatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BusRepositoryTest {

    @Autowired
    private BusRepository busRepository;

    private final LocalDate travelDate = LocalDate.now().plusDays(1);

    @BeforeEach
    void setUp() {
        busRepository.save(busWithSeats("Bus With Window", "Ongole", "Chennai", travelDate,
                new BigDecimal("850"), BusType.AC_SLEEPER, LocalTime.of(20, 30), true));

        busRepository.save(busWithSeats("Bus Without Window", "Ongole", "Chennai", travelDate,
                new BigDecimal("700"), BusType.AC_SLEEPER, LocalTime.of(21, 0), false));
    }

    @Test
    void findsOnlyBusesWithAvailableWindowSeat() {
        List<Bus> results = busRepository.searchWithSeatFilter(
                "Ongole", "Chennai", travelDate, SeatType.WINDOW, null, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getBusName()).isEqualTo("Bus With Window");
    }

    @Test
    void appliesMaxPriceFilter() {
        List<Bus> results = busRepository.searchWithSeatFilter(
                "Ongole", "Chennai", travelDate, SeatType.WINDOW, new BigDecimal("800"), null, null, null);

        assertThat(results).isEmpty(); // the only bus with a window seat costs 850
    }

    @Test
    void plainSearchIgnoresSeatAvailability() {
        List<Bus> results = busRepository
                .findBySourceIgnoreCaseAndDestinationIgnoreCaseAndTravelDate("Ongole", "Chennai", travelDate);

        assertThat(results).hasSize(2);
    }

    private Bus busWithSeats(String name, String source, String destination, LocalDate date,
                              BigDecimal price, BusType busType, LocalTime departure, boolean hasAvailableWindow) {
        Bus bus = Bus.builder()
                .busName(name)
                .operatorName(name + " Operator")
                .source(source)
                .destination(destination)
                .travelDate(date)
                .departureTime(departure)
                .arrivalTime(departure.plusHours(8))
                .busType(busType)
                .price(price)
                .totalSeats(2)
                .build();

        bus.addSeat(Seat.builder().seatNumber("1A").seatType(SeatType.WINDOW).deck("LOWER")
                .available(hasAvailableWindow).build());
        bus.addSeat(Seat.builder().seatNumber("1B").seatType(SeatType.AISLE).deck("LOWER")
                .available(true).build());

        return bus;
    }
}

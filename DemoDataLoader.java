package com.example.busai.config;

import com.example.busai.entity.*;
import com.example.busai.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

/**
 * Inserts realistic demo buses + seats on startup (only if the table is
 * empty), so the AI tools have real data to search over. Some buses are
 * deliberately given zero available window seats so filtering behavior can
 * be demonstrated end-to-end.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataLoader implements CommandLineRunner {

    private final BusRepository busRepository;
    private final Random random = new Random(42);

    private record RouteBus(String source, String destination, String busName, String operator,
                             BusType busType, LocalTime departure, LocalTime arrival, BigDecimal price,
                             boolean starveWindowSeats) {
    }

    @Override
    public void run(String... args) {
        if (busRepository.count() > 0) {
            log.info("Demo data already present, skipping DemoDataLoader.");
            return;
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<RouteBus> routeBuses = List.of(
                new RouteBus("Ongole", "Chennai", "Orange Travels", "Orange Travels Pvt Ltd",
                        BusType.AC_SLEEPER, LocalTime.of(20, 30), LocalTime.of(5, 30), new BigDecimal("850"), false),
                new RouteBus("Ongole", "Chennai", "VRL Travels", "VRL Logistics",
                        BusType.AC_SLEEPER, LocalTime.of(21, 15), LocalTime.of(6, 0), new BigDecimal("920"), false),
                new RouteBus("Ongole", "Chennai", "IntrCity SmartBus", "IntrCity",
                        BusType.AC_SEATER, LocalTime.of(22, 0), LocalTime.of(6, 30), new BigDecimal("780"), false),
                new RouteBus("Ongole", "Chennai", "SRS Travels", "SRS Travels",
                        BusType.NON_AC_SLEEPER, LocalTime.of(23, 0), LocalTime.of(7, 0), new BigDecimal("950"), false),
                new RouteBus("Ongole", "Chennai", "Kaveri Travels", "Kaveri Travels",
                        BusType.NON_AC_SEATER, LocalTime.of(14, 0), LocalTime.of(21, 30), new BigDecimal("550"), true),
                new RouteBus("Nellore", "Chennai", "Parveen Travels", "Parveen Travels",
                        BusType.AC_SLEEPER, LocalTime.of(22, 30), LocalTime.of(5, 0), new BigDecimal("700"), false),
                new RouteBus("Vijayawada", "Hyderabad", "Orange Travels", "Orange Travels Pvt Ltd",
                        BusType.AC_SEATER, LocalTime.of(9, 0), LocalTime.of(13, 30), new BigDecimal("650"), false),
                new RouteBus("Vijayawada", "Bangalore", "VRL Travels", "VRL Logistics",
                        BusType.AC_SLEEPER, LocalTime.of(19, 0), LocalTime.of(7, 0), new BigDecimal("1100"), true),
                new RouteBus("Hyderabad", "Bangalore", "IntrCity SmartBus", "IntrCity",
                        BusType.AC_SLEEPER, LocalTime.of(21, 0), LocalTime.of(8, 0), new BigDecimal("1250"), false),
                new RouteBus("Ongole", "Bangalore", "SRS Travels", "SRS Travels",
                        BusType.NON_AC_SLEEPER, LocalTime.of(20, 0), LocalTime.of(6, 0), new BigDecimal("890"), false)
        );

        for (RouteBus rb : routeBuses) {
            Bus bus = Bus.builder()
                    .busName(rb.busName())
                    .operatorName(rb.operator())
                    .source(rb.source())
                    .destination(rb.destination())
                    .travelDate(tomorrow)
                    .departureTime(rb.departure())
                    .arrivalTime(rb.arrival())
                    .busType(rb.busType())
                    .price(rb.price())
                    .totalSeats(36)
                    .build();

            for (int i = 1; i <= 36; i++) {
                SeatType type = seatTypeForPosition(i);
                boolean available;
                if (type == SeatType.WINDOW && rb.starveWindowSeats()) {
                    available = false; // this bus has no available window seats, on purpose
                } else {
                    available = random.nextInt(100) < 65; // ~65% available
                }
                Seat seat = Seat.builder()
                        .seatNumber(seatNumber(i))
                        .seatType(type)
                        .deck(i <= 18 ? "LOWER" : "UPPER")
                        .available(available)
                        .build();
                bus.addSeat(seat);
            }

            busRepository.save(bus);
        }

        log.info("Demo data loaded: {} buses inserted for travel date {}", routeBuses.size(), tomorrow);
    }

    private SeatType seatTypeForPosition(int index) {
        int mod = index % 3;
        return switch (mod) {
            case 1 -> SeatType.WINDOW;
            case 2 -> SeatType.MIDDLE;
            default -> SeatType.AISLE;
        };
    }

    private String seatNumber(int index) {
        // Produces numbers like "1A", "1B", "1C", "2A" ... matching typical bus seat labels.
        int row = ((index - 1) / 3) + 1;
        char col = (char) ('A' + ((index - 1) % 3));
        return row + "" + col;
    }
}

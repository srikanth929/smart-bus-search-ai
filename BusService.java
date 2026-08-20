package com.example.busai.service;

import com.example.busai.dto.BusResponse;
import com.example.busai.dto.BusSearchCriteria;
import com.example.busai.dto.SeatResponse;
import com.example.busai.entity.Bus;
import com.example.busai.entity.Seat;
import com.example.busai.exception.BusSearchException;
import com.example.busai.exception.ResourceNotFoundException;
import com.example.busai.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    /**
     * Plain route + date search (GET /api/buses/search).
     */
    @Transactional(readOnly = true)
    public List<BusResponse> searchBuses(String source, String destination, LocalDate date) {
        validateSearch(source, destination, date);

        List<Bus> buses = busRepository
                .findBySourceIgnoreCaseAndDestinationIgnoreCaseAndTravelDate(source, destination, date);

        if (buses.isEmpty()) {
            log.info("No buses found for {} -> {} on {}", source, destination, date);
        }

        return buses.stream().map(this::toBusResponse).toList();
    }

    /**
     * Optimized search that only returns buses satisfying the seat / price /
     * bus-type / departure-time preferences. This is the method used both by
     * GET /api/buses/search-with-seats and by the AI tool.
     */
    @Transactional(readOnly = true)
    public List<BusResponse> searchBusesWithCriteria(BusSearchCriteria criteria) {
        validateSearch(criteria.getSource(), criteria.getDestination(), criteria.getDate());

        List<Bus> buses = busRepository.searchWithSeatFilter(
                criteria.getSource(),
                criteria.getDestination(),
                criteria.getDate(),
                criteria.getSeatType(),
                criteria.getMaxPrice(),
                criteria.getBusType(),
                criteria.getDepartureAfter(),
                criteria.getDepartureBefore()
        );

        return buses.stream()
                .map(bus -> toBusResponseWithMatchingSeats(bus, criteria))
                .toList();
    }

    @Transactional(readOnly = true)
    public Bus getBusOrThrow(Long busId) {
        return busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));
    }

    private void validateSearch(String source, String destination, LocalDate date) {
        if (source == null || source.isBlank() || destination == null || destination.isBlank()) {
            throw new BusSearchException("Source and destination are required.");
        }
        if (source.equalsIgnoreCase(destination)) {
            throw new BusSearchException("Source and destination cannot be the same.");
        }
        if (date == null) {
            throw new BusSearchException("Travel date is required.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new BusSearchException("Travel date cannot be in the past.");
        }
    }

    private BusResponse toBusResponse(Bus bus) {
        return BusResponse.builder()
                .id(bus.getId())
                .busName(bus.getBusName())
                .operatorName(bus.getOperatorName())
                .source(bus.getSource())
                .destination(bus.getDestination())
                .travelDate(bus.getTravelDate())
                .departureTime(bus.getDepartureTime())
                .arrivalTime(bus.getArrivalTime())
                .busType(bus.getBusType())
                .price(bus.getPrice())
                .totalSeats(bus.getTotalSeats())
                .build();
    }

    private BusResponse toBusResponseWithMatchingSeats(Bus bus, BusSearchCriteria criteria) {
        BusResponse response = toBusResponse(bus);
        if (criteria.getSeatType() != null) {
            List<SeatResponse> matching = bus.getSeats().stream()
                    .filter(Seat::isAvailable)
                    .filter(s -> s.getSeatType() == criteria.getSeatType())
                    .map(s -> SeatResponse.builder()
                            .id(s.getId())
                            .seatNumber(s.getSeatNumber())
                            .seatType(s.getSeatType())
                            .deck(s.getDeck())
                            .available(s.isAvailable())
                            .build())
                    .toList();
            response = BusResponse.builder()
                    .id(response.getId())
                    .busName(response.getBusName())
                    .operatorName(response.getOperatorName())
                    .source(response.getSource())
                    .destination(response.getDestination())
                    .travelDate(response.getTravelDate())
                    .departureTime(response.getDepartureTime())
                    .arrivalTime(response.getArrivalTime())
                    .busType(response.getBusType())
                    .price(response.getPrice())
                    .totalSeats(response.getTotalSeats())
                    .matchingSeats(matching)
                    .build();
        }
        return response;
    }
}

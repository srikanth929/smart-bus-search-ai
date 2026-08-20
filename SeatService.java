package com.example.busai.service;

import com.example.busai.dto.SeatResponse;
import com.example.busai.entity.Seat;
import com.example.busai.entity.SeatType;
import com.example.busai.exception.ResourceNotFoundException;
import com.example.busai.exception.SeatUnavailableException;
import com.example.busai.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public List<SeatResponse> getAllSeats(Long busId) {
        return seatRepository.findByBusId(busId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getAvailableSeatsByType(Long busId, SeatType seatType) {
        return seatRepository.findByBusIdAndSeatTypeAndAvailableTrue(busId, seatType).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Design stub for the future Booking Service (see requirement #16).
     *
     * Concurrency safety comes from two layers:
     *  1. A pessimistic write lock (SELECT ... FOR UPDATE) fetched via
     *     seatRepository.findByIdForUpdate() so only one transaction at a
     *     time can inspect/modify a given seat row.
     *  2. The @Version column on Seat as a defense-in-depth optimistic
     *     check, in case the pessimistic lock is ever bypassed (e.g. a
     *     different code path updates the row).
     *
     * If two users race to book the same seat, the second transaction
     * blocks on the row lock until the first commits, then sees
     * available = false and gets a SeatUnavailableException.
     */
    @Transactional
    public Seat bookSeat(Long seatId) {
        Seat seat = seatRepository.findByIdForUpdate(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));

        if (!seat.isAvailable()) {
            throw new SeatUnavailableException(
                    "Seat " + seat.getSeatNumber() + " is no longer available. Please choose another seat.");
        }

        seat.setAvailable(false);
        Seat saved = seatRepository.save(seat);
        log.info("Seat {} on bus {} booked successfully", saved.getSeatNumber(), saved.getBus().getId());
        return saved;
    }

    private SeatResponse toResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .deck(seat.getDeck())
                .available(seat.isAvailable())
                .build();
    }
}

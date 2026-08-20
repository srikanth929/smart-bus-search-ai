package com.example.busai.repository;

import com.example.busai.entity.Seat;
import com.example.busai.entity.SeatType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    List<Seat> findByBusIdAndSeatTypeAndAvailableTrue(Long busId, SeatType seatType);

    /**
     * Row-level lock used by SeatService#bookSeat so that two concurrent
     * booking requests for the same seat cannot both succeed
     * (works together with the @Version optimistic-lock field on Seat).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    Optional<Seat> findByIdForUpdate(@Param("seatId") Long seatId);
}

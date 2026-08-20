package com.example.busai.repository;

import com.example.busai.entity.Bus;
import com.example.busai.entity.BusType;
import com.example.busai.entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {

    /**
     * Plain search: all buses on a route/date, regardless of seat availability.
     */
    List<Bus> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndTravelDate(
            String source, String destination, LocalDate travelDate);

    /**
     * Optimized search used by /search-with-seats and by the AI tool.
     * Returns only buses that have at least one seat matching the given
     * seatType (when provided) and available = true, plus the optional
     * price / busType / departure-time-window filters.
     *
     * All filters are optional (nullable) - pass null to skip a filter.
     */
    @Query("""
            SELECT DISTINCT b FROM Bus b
            JOIN b.seats s
            WHERE LOWER(b.source) = LOWER(:source)
              AND LOWER(b.destination) = LOWER(:destination)
              AND b.travelDate = :date
              AND (:seatType IS NULL OR s.seatType = :seatType)
              AND (:seatType IS NULL OR s.available = true)
              AND (:maxPrice IS NULL OR b.price <= :maxPrice)
              AND (:busType IS NULL OR b.busType = :busType)
              AND (:departureAfter IS NULL OR b.departureTime >= :departureAfter)
              AND (:departureBefore IS NULL OR b.departureTime <= :departureBefore)
            ORDER BY b.price ASC
            """)
    List<Bus> searchWithSeatFilter(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("date") LocalDate date,
            @Param("seatType") SeatType seatType,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("busType") BusType busType,
            @Param("departureAfter") LocalTime departureAfter,
            @Param("departureBefore") LocalTime departureBefore
    );
}

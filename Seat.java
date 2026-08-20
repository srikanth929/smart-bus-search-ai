package com.example.busai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", indexes = {
        @Index(name = "idx_bus_seattype_available", columnList = "bus_id,seatType,available")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "bus")
@ToString(exclude = "bus")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @Column(nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @Column(nullable = false)
    private String deck; // e.g. LOWER, UPPER

    @Column(nullable = false)
    private boolean available;

    /**
     * Optimistic-locking version column. Prevents two concurrent requests
     * from both successfully booking the same seat (see SeatService#bookSeat).
     */
    @Version
    private Long version;
}

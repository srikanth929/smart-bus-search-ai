package com.example.busai.service;

import com.example.busai.entity.Seat;
import com.example.busai.entity.SeatType;
import com.example.busai.exception.ResourceNotFoundException;
import com.example.busai.exception.SeatUnavailableException;
import com.example.busai.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    @Test
    void bookSeat_marksAvailableSeatAsBooked() {
        Seat seat = Seat.builder().id(1L).seatNumber("12A").seatType(SeatType.WINDOW).available(true).build();
        when(seatRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(seat));
        when(seatRepository.save(seat)).thenReturn(seat);

        Seat booked = seatService.bookSeat(1L);

        assertThat(booked.isAvailable()).isFalse();
    }

    @Test
    void bookSeat_throwsWhenSeatAlreadyBooked() {
        Seat seat = Seat.builder().id(1L).seatNumber("12A").seatType(SeatType.WINDOW).available(false).build();
        when(seatRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(seat));

        assertThatThrownBy(() -> seatService.bookSeat(1L))
                .isInstanceOf(SeatUnavailableException.class);
    }

    @Test
    void bookSeat_throwsWhenSeatDoesNotExist() {
        when(seatRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.bookSeat(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {
    BookingDto save(Long userId, BookingShortDto bookingShortDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByBookerId(Long userId, String state, Integer from, Integer size);

    List<BookingDto> findAllByOwnerId(Long userId, String state, Integer from, Integer size);
}

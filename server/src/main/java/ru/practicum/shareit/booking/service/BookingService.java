package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto approve(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByBookerId(Long bookerId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwnerId(Long ownerId, String state, Integer from, Integer size);
}

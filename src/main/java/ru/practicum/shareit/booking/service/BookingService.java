package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingService {
    Booking save(Long userId, Booking booking, Long itemId);

    Booking approve(Long userId, Long bookingId, Boolean approved);

    Booking findById(Long userId, Long bookingId);

    List<Booking> findAllByBookerId(Long userId, String state, Integer from, Integer size);

    List<Booking> findAllByOwnerId(Long userId, String state, Integer from, Integer size);
}

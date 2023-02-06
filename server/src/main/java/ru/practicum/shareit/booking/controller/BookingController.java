package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping()
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody BookingRequestDto bookingRequestDto) {
        return service.save(userId, bookingRequestDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return service.getById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @RequestParam(required = false) String state,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        return service.getAllByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(required = false) String state,
                                            @RequestParam(required = false) Integer from,
                                            @RequestParam(required = false) Integer size) {
        return service.getAllByOwnerId(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam Boolean approved) {
        return service.approve(ownerId, bookingId, approved);
    }
}

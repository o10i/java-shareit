package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.booking.BookingMapper.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServiceImpl service;

    @PostMapping()
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody BookingInDto bookingInDto) {
        return toBookingDto(service.save(userId, toBooking(bookingInDto), bookingInDto.getItemId()));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        return toBookingDto(service.approve(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        return toBookingDto(service.findById(userId, bookingId));
    }

    @GetMapping()
    public List<BookingDto> findAllByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return toListBookingDto(service.findAllByBookerId(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return toListBookingDto(service.findAllByOwnerId(ownerId, state, from, size));
    }
}

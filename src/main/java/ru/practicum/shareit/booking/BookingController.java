package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServiceImpl service;
    @PostMapping()
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @Valid @RequestBody BookingDto bookingDto) {
        return service.save(userId, bookingDto);
    }
}

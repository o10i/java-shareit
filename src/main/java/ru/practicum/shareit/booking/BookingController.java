package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServiceImpl service;

    @PostMapping()
    public BookingOutDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody BookingInDto bookingInDto) {
        return service.save(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam Boolean approved) {
        return service.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        return service.findById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingOutDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(required = false, defaultValue = "ALL") String state) {
        return service.findAll(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> findAllOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return service.findAllOwner(userId, state);
    }
}

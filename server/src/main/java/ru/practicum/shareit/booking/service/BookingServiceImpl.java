package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    BookingRepository repository;
    ItemServiceImpl itemService;
    UserServiceImpl userService;

    @Transactional
    @Override
    public BookingDto add(Long userId, BookingRequestDto bookingRequestDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();
        if (!start.isBefore(end) || !start.isAfter(now)) {
            throw new BadRequestException(String.format("start=%s or end=%s has invalid value.", start, end));
        }

        Item item = itemService.findByIdWithCheck(bookingRequestDto.getItemId());
        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException(String.format("userId=%d equals ownerId=%d.", userId, item.getOwnerId()));
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Item with id=%d unavailable.", item.getId()));
        }

        User booker = userService.findByIdWithCheck(userId);
        return toBookingDto(repository.save(toBooking(bookingRequestDto, item, booker)));
    }

    @Transactional
    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        userService.findByIdWithCheck(userId);

        Booking booking = findByIdWithCheck(bookingId);
        if (!userId.equals(booking.getItem().getOwnerId())) {
            throw new NotFoundException(String.format("userId=%d not equal to ownerId=%d", userId, booking.getItem().getOwnerId()));
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException(String.format("Booking with id=%d hasn't WAITING status.", booking.getId()));
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        userService.findByIdWithCheck(userId);

        Booking booking = findByIdWithCheck(bookingId);

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwnerId();
        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d not equal to bookerId=%d or ownerId=%d", userId, bookerId, ownerId));
        }

        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        userService.findByIdWithCheck(userId);

        List<Booking> bookings = List.of();
        switch (state) {
            case "ALL":
                bookings = repository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
            case "REJECTED":
                bookings = repository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, Status.valueOf(state));
                break;
        }
        return toListBookingDto(bookings.stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        userService.findByIdWithCheck(userId);

        List<Booking> bookings = List.of();
        switch (state) {
            case "ALL":
                bookings = repository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
            case "REJECTED":
                bookings = repository.findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, Status.valueOf(state));
                break;
        }
        return toListBookingDto(bookings.stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    private Booking findByIdWithCheck(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
    }
}

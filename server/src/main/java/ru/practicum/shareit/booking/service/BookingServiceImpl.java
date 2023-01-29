package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.*;

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
    public BookingDto save(Long userId, BookingRequestDto bookingRequestDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();
        if (!start.isBefore(end) || !start.isAfter(now)) {
            throw new BadRequestException(String.format("start=%s or end=%s has invalid value.", start, end));
        }

        Item item = itemService.getByIdWithCheck(bookingRequestDto.getItemId());
        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException(String.format("userId=%d equals ownerId=%d.", userId, item.getOwnerId()));
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Item with id=%d unavailable.", item.getId()));
        }

        User booker = userService.getByIdWithCheck(userId);
        return toBookingDto(repository.save(toBooking(bookingRequestDto, item, booker)));
    }

    @Transactional
    @Override
    public BookingDto approve(Long ownerId, Long bookingId, Boolean approved) {
        userService.getByIdWithCheck(ownerId);

        Booking booking = findByIdWithCheck(bookingId);
        Long trueOwnerId = booking.getItem().getOwnerId();
        if (!ownerId.equals(trueOwnerId)) {
            throw new NotFoundException(String.format("ownerId=%d not equal to true ownerId=%d", ownerId, trueOwnerId));
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException(String.format("Booking with id=%d hasn't WAITING status.", booking.getId()));
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        userService.getByIdWithCheck(userId);

        Booking booking = findByIdWithCheck(bookingId);

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwnerId();
        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d not equal to bookerId=%d or ownerId=%d", userId, bookerId, ownerId));
        }

        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBookerId(Long bookerId, String state, Integer from, Integer size) {
        userService.getByIdWithCheck(bookerId);

        List<Booking> bookings = List.of();
        switch (state) {
            case "ALL":
                bookings = repository.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
            case "CURRENT":
                bookings = repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
                break;
            case "WAITING":
            case "REJECTED":
                bookings = repository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(bookerId, Status.valueOf(state));
                break;
        }
        return toBookingDtoList(bookings.stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    @Override
    public List<BookingDto> getAllByOwnerId(Long ownerId, String state, Integer from, Integer size) {
        userService.getByIdWithCheck(ownerId);

        List<Booking> bookings = List.of();
        switch (state) {
            case "ALL":
                bookings = repository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "CURRENT":
                bookings = repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "WAITING":
            case "REJECTED":
                bookings = repository.findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(ownerId, Status.valueOf(state));
                break;
        }
        return toBookingDtoList(bookings.stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    private Booking findByIdWithCheck(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
    }
}

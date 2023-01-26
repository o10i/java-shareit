package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
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
    public BookingDto add(Long userId, BookItemRequestDto bookItemRequestDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookItemRequestDto.getStart();
        LocalDateTime end = bookItemRequestDto.getEnd();
        if (!start.isBefore(end) || !start.isAfter(now)) {
            throw new BadRequestException(String.format("start=%s or end=%s has invalid value.", start, end));
        }

        Item item = itemService.findByIdWithCheck(bookItemRequestDto.getItemId());
        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException(String.format("userId=%d equals ownerId=%d.", userId, item.getOwnerId()));
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Item with id=%d unavailable.", item.getId()));
        }

        User booker = userService.findByIdWithCheck(userId);
        return toBookingDto(repository.save(toBooking(bookItemRequestDto, item, booker)));
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
    public List<BookingDto> getAllByBooker(Long bookerId, String state, Integer from, Integer size) {
        userService.findByIdWithCheck(bookerId);

        PageRequest pageable = PageRequest.of(from / size, size);

        switch (state) {
            case "ALL":
                return toListBookingDto(repository.findAllByBookerIdOrderByStartDesc(bookerId, pageable).getContent());
            case "CURRENT":
                return toListBookingDto(repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent());
            case "PAST":
                return toListBookingDto(repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), pageable).getContent());
            case "FUTURE":
                return toListBookingDto(repository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now(), pageable).getContent());
            case "WAITING":
            case "REJECTED":
                return toListBookingDto(repository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(bookerId, Status.valueOf(state), pageable).getContent());
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, String state, Integer from, Integer size) {
        userService.findByIdWithCheck(ownerId);

        PageRequest pageable = PageRequest.of(from / size, size);

        switch (state) {
            case "ALL":
                return toListBookingDto(repository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable).getContent());
            case "CURRENT":
                return toListBookingDto(repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent());
            case "PAST":
                return toListBookingDto(repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable).getContent());
            case "FUTURE":
                return toListBookingDto(repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable).getContent());
            case "WAITING":
            case "REJECTED":
                return toListBookingDto(repository.findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(ownerId, Status.valueOf(state), pageable).getContent());
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    private Booking findByIdWithCheck(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
    }
}

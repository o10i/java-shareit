package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

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
    public Booking save(Long userId, Booking booking, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        if (!start.isBefore(end) || !start.isAfter(now)) {
            throw new BadRequestException(String.format("start=%s or end=%s has invalid value.", start, end));
        }

        Item item = itemService.findByIdWithCheck(itemId);
        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException(String.format("userId=%d equals ownerId=%d.", userId, item.getOwnerId()));
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Item with id=%d unavailable.", item.getId()));
        }

        User booker = userService.findByIdWithCheck(userId);

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        return repository.save(booking);
    }

    @Transactional
    @Override
    public Booking approve(Long userId, Long bookingId, Boolean approved) {
        userService.findByIdWithCheck(userId);

        Booking booking = findByIdWithCheck(bookingId);
        if (!userId.equals(booking.getItem().getOwnerId())) {
            throw new NotFoundException(String.format("userId=%d not equal to ownerId=%d", userId, booking.getItem().getOwnerId()));
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException(String.format("Booking with id=%d already approved.", booking.getId()));
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return booking;
    }

    @Override
    public Booking findById(Long userId, Long bookingId) {
        userService.findByIdWithCheck(userId);

        Booking booking = findByIdWithCheck(bookingId);

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwnerId();
        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d not equal to bookerId=%d or ownerId=%d", userId, bookerId, ownerId));
        }

        return booking;
    }

    @Override
    public List<Booking> findAllByBookerId(Long userId, String state, Integer from, Integer size) {
        userService.findByIdWithCheck(userId);

        PageRequest pageable = PageRequest.of(from / size, size);

        switch (state) {
            case "ALL":
                return repository.findAllByBooker_IdOrderByStartDesc(userId, pageable).getContent();
            case "CURRENT":
                return repository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent();
            case "PAST":
                return repository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable).getContent();
            case "FUTURE":
                return repository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable).getContent();
            case "WAITING":
            case "REJECTED":
                return repository.findAllByBooker_IdAndStatusEqualsOrderByStartDesc(userId, Status.valueOf(state), pageable).getContent();
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<Booking> findAllByOwnerId(Long ownerId, String state, Integer from, Integer size) {
        userService.findByIdWithCheck(ownerId);

        PageRequest pageable = PageRequest.of(from / size, size);

        switch (state) {
            case "ALL":
                return repository.findAllByItem_OwnerIdOrderByStartDesc(ownerId, pageable).getContent();
            case "CURRENT":
                return repository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent();
            case "PAST":
                return repository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable).getContent();
            case "FUTURE":
                return repository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable).getContent();
            case "WAITING":
            case "REJECTED":
                return repository.findAllByItem_OwnerIdAndStatusEqualsOrderByStartDesc(ownerId, Status.valueOf(state), pageable).getContent();
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    private Booking findByIdWithCheck(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
    }
}

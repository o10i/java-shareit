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
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
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
        checkBookingDates(booking);

        Item item = itemService.findByIdWithException(itemId);
        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException(String.format("userId=%d equals ownerId=%d.", userId, item.getOwnerId()));
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Item with id=%d unavailable.", item.getId()));
        }

        User booker = userService.findById(userId);

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        return repository.save(booking);
    }

    @Transactional
    @Override
    public Booking approve(Long userId, Long bookingId, Boolean approved) {
        userService.findById(userId);
        Booking booking = findByIdWithException(bookingId);

        checkItemOwnerForApprove(userId, booking);
        checkBookingNotApproved(booking);

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        repository.save(booking);

        UserDto userDto = UserMapper.toUserDto(userService.findById(booking.getBooker().getId()));
        ItemBookingDto itemBookingDto = itemService.findById(userId, booking.getItem().getId());

        return booking;
    }

    @Override
    public Booking findById(Long userId, Long bookingId) {
        userService.findById(userId);
        Booking booking = findByIdWithException(bookingId);

        Long bookerId = booking.getBooker().getId();
        Long ownerId = itemService.findByIdWithException(booking.getItem().getId()).getOwnerId();
        checkUser(userId, bookerId, ownerId);

        UserDto userDto = UserMapper.toUserDto(userService.findById(bookerId));
        ItemBookingDto itemBookingDto = itemService.findById(userId, booking.getItem().getId());

        return booking;
    }

    @Override
    public List<Booking> findAllByBookerId(Long userId, String state, Integer from, Integer size) {
        userService.findById(userId);

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
        userService.findById(ownerId);

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

    private Booking findByIdWithException(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
    }


    private void checkItemOwnerForApprove(Long userId, Booking booking) {
        Long ownerId = itemService.findByIdWithException(booking.getItem().getId()).getOwnerId();

        if (!userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d not equal to ownerId=%d", userId, ownerId));
        }
    }

    private void checkBookingNotApproved(Booking booking) {
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException(String.format("Booking with id=%d already approved.", booking.getId()));
        }
    }

    private void checkBookingDates(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        if (!start.isBefore(end) || !start.isAfter(now)) {
            throw new BadRequestException(String.format("start=%s or end=%s has invalid value.", start, end));
        }
    }

    private void checkUser(Long userId, Long bookerId, Long ownerId) {
        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d not equal to bookerId=%d or ownerId=%d", userId, bookerId, ownerId));
        }
    }
}

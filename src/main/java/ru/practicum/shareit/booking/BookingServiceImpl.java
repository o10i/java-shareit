package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    BookingRepository repository;
    ItemServiceImpl itemService;
    UserServiceImpl userService;

    @Override
    public BookingOutDto save(Long userId, BookingInDto bookingInDto) {
        Long ownerId = itemService.findByIdWithException(bookingInDto.getItemId()).getOwnerId();

        if (userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d equals ownerId=%d.", userId, ownerId));
        }

        Long itemId = bookingInDto.getItemId();
        ItemDto item = itemService.findById(userId, itemId);

        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Item with id=%d unavailable.", itemId));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingInDto.getStart();
        LocalDateTime end = bookingInDto.getEnd();
        if (start.isBefore(now) || end.isBefore(now) || end.isBefore(start)) {
            throw new BadRequestException(String.format("start=%s or end=%s has invalid value.", start, end));
        }

        UserDto booker = userService.findById(userId);

        Booking booking = BookingMapper.toBooking(bookingInDto);
        booking.setBookerId(userId);
        booking.setStatus(Status.WAITING);
        repository.save(booking);
        return toFullBookingOutDto(booking, booker, item);
    }

    @Override
    public BookingOutDto approve(Long userId, Long bookingId, Boolean approved) {
        userService.findByIdWithException(userId);
        Booking booking = findByIdWithException(bookingId);

        Long ownerId = itemService.findByIdWithException(booking.getItemId()).getOwnerId();
        if (!userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d not equal to ownerId=%d", userId, ownerId));
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException(String.format("Booking with id=%d already approved.", bookingId));
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        repository.save(booking);
        return toFullBookingOutDto(booking,
                userService.findById(booking.getBookerId()),
                itemService.findById(userId, booking.getItemId()));
    }

    @Override
    public BookingOutDto findById(Long userId, Long bookingId) {
        userService.findByIdWithException(userId);
        Booking booking = findByIdWithException(bookingId);

        Long bookerId = booking.getBookerId();
        Long ownerId = itemService.findByIdWithException(booking.getItemId()).getOwnerId();
        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d not equal to bookerId=%d or ownerId=%d", userId, bookerId, ownerId));
        }
        return toFullBookingOutDto(booking,
                userService.findById(bookerId),
                itemService.findById(userId, booking.getItemId()));
    }

    @Override
    public List<BookingOutDto> findAll(Long userId, String state, Integer from, Integer size) {
        userService.findByIdWithException(userId);

        State st = Arrays.stream(State.values()).filter(s -> s.name().equals(state)).findFirst()
                .orElseThrow(() -> new BadRequestException(String.format("Unknown state: %s", state)));

        PageRequest pageable = PageRequest.of(from / size, size);

        switch (st) {
            case ALL:
                return toFullBookingsOutDto(userId, repository.findAllByBookerIdOrderByStartDesc(userId, pageable).getContent());
            case CURRENT:
                return toFullBookingsOutDto(userId, repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent());
            case PAST:
                return toFullBookingsOutDto(userId, repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable).getContent());
            case FUTURE:
                return toFullBookingsOutDto(userId, repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable).getContent());
            case WAITING:
            case REJECTED:
                return toFullBookingsOutDto(userId, repository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, Status.valueOf(st.name()), pageable).getContent());
        }
        return new ArrayList<>();
    }

    @Override
    public List<BookingOutDto> findAllOwner(Long userId, String state, Integer from, Integer size) {
        userService.findByIdWithException(userId);

        State st = Arrays.stream(State.values()).filter(s -> s.name().equals(state)).findFirst()
                .orElseThrow(() -> new BadRequestException(String.format("Unknown state: %s", state)));

        PageRequest pageable = PageRequest.of(from / size, size);

        switch (st) {
            case ALL:
                return toFullBookingsOutDto(userId, repository.findAllByOwnerOrderByStartDesc(userId, pageable).getContent());
            case CURRENT:
                return toFullBookingsOutDto(userId, repository.findAllByOwnerAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent());
            case PAST:
                return toFullBookingsOutDto(userId, repository.findAllByOwnerAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable).getContent());
            case FUTURE:
                return toFullBookingsOutDto(userId, repository.findAllByOwnerAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable).getContent());
            case WAITING:
            case REJECTED:
                return toFullBookingsOutDto(userId, repository.findAllByOwnerAndStatusEqualsOrderByStartDesc(userId, Status.valueOf(st.name()), pageable).getContent());
        }
        return new ArrayList<>();
    }

    private Booking findByIdWithException(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
    }

    private BookingOutDto toFullBookingOutDto(Booking booking, UserDto booker, ItemDto item) {
        BookingOutDto bookingOutDto = BookingMapper.toBookingOutDto(booking);
        bookingOutDto.setBooker(booker);
        bookingOutDto.setItem(item);
        return bookingOutDto;
    }

    private List<BookingOutDto> toFullBookingsOutDto(Long userId, List<Booking> bookings) {
        return bookings.stream().map(booking -> toFullBookingOutDto(booking,
                        userService.findById(booking.getBookerId()),
                        itemService.findById(userId, booking.getItemId())))
                .collect(Collectors.toList());
    }
}
package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
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
    public BookingDto save(Long userId, BookingSaveDto bookingSaveDto) {
        checkItemOwnerForSave(userId, bookingSaveDto);
        checkBookingDates(bookingSaveDto);

        Item item = itemService.findByIdWithException(bookingSaveDto.getItemId());
        checkItemAvailable(item);

        UserDto bookerDto = UserMapper.toUserDto(userService.findById(userId));
        ItemBookingDto itemBookingDto = itemService.findById(userId, bookingSaveDto.getItemId());

        Booking booking = BookingMapper.toBooking(bookingSaveDto);
        booking.setItem(item);
        booking.setBooker(userService.findById(userId));
        booking.setStatus(Status.WAITING);
        repository.save(booking);

        return BookingMapper.toBookingDto(booking, bookerDto, itemBookingDto);
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        userService.findById(userId);
        Booking booking = findByIdWithException(bookingId);

        checkItemOwnerForApprove(userId, booking);
        checkBookingNotApproved(booking);

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        repository.save(booking);

        UserDto userDto = UserMapper.toUserDto(userService.findById(booking.getBooker().getId()));
        ItemBookingDto itemBookingDto = itemService.findById(userId, booking.getItem().getId());

        return BookingMapper.toBookingDto(booking, userDto, itemBookingDto);
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        userService.findById(userId);
        Booking booking = findByIdWithException(bookingId);

        Long bookerId = booking.getBooker().getId();
        Long ownerId = itemService.findByIdWithException(booking.getItem().getId()).getOwnerId();
        checkUser(userId, bookerId, ownerId);

        UserDto userDto = UserMapper.toUserDto(userService.findById(bookerId));
        ItemBookingDto itemBookingDto = itemService.findById(userId, booking.getItem().getId());

        return BookingMapper.toBookingDto(booking, userDto, itemBookingDto);
    }

    @Override
    public List<BookingDto> findAllByBookerId(Long userId, String state, Integer from, Integer size) {
        userService.findById(userId);

        PageRequest pageable = PageRequest.of(from / size, size);

        switch (state) {
            case "ALL":
                return toFullBookingsDto(userId, repository.findAllByBooker_IdOrderByStartDesc(userId, pageable).getContent());
            case "CURRENT":
                return toFullBookingsDto(userId, repository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent());
            case "PAST":
                return toFullBookingsDto(userId, repository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable).getContent());
            case "FUTURE":
                return toFullBookingsDto(userId, repository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable).getContent());
            case "WAITING":
            case "REJECTED":
                return toFullBookingsDto(userId, repository.findAllByBooker_IdAndStatusEqualsOrderByStartDesc(userId, Status.valueOf(state), pageable).getContent());
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingDto> findAllByOwnerId(Long ownerId, String state, Integer from, Integer size) {
        userService.findById(ownerId);

        PageRequest pageable = PageRequest.of(from / size, size);

        switch (state) {
            case "ALL":
                return toFullBookingsDto(ownerId, repository.findAllByItem_OwnerIdOrderByStartDesc(ownerId, pageable).getContent());
            case "CURRENT":
                return toFullBookingsDto(ownerId, repository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent());
            case "PAST":
                return toFullBookingsDto(ownerId, repository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable).getContent());
            case "FUTURE":
                return toFullBookingsDto(ownerId, repository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable).getContent());
            case "WAITING":
            case "REJECTED":
                return toFullBookingsDto(ownerId, repository.findAllByItem_OwnerIdAndStatusEqualsOrderByStartDesc(ownerId, Status.valueOf(state), pageable).getContent());
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    private Booking findByIdWithException(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
    }

    private void checkItemOwnerForSave(Long userId, BookingSaveDto bookingSaveDto) {
        Long ownerId = itemService.findByIdWithException(bookingSaveDto.getItemId()).getOwnerId();

        if (userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d equals ownerId=%d.", userId, ownerId));
        }
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

    private void checkBookingDates(BookingSaveDto bookingSaveDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingSaveDto.getStart();
        LocalDateTime end = bookingSaveDto.getEnd();
        if (!start.isBefore(end) || !start.isAfter(now)) {
            throw new BadRequestException(String.format("start=%s or end=%s has invalid value.", start, end));
        }
    }

    private void checkItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Item with id=%d unavailable.", item.getId()));
        }
    }

    private void checkUser(Long userId, Long bookerId, Long ownerId) {
        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException(String.format("userId=%d not equal to bookerId=%d or ownerId=%d", userId, bookerId, ownerId));
        }
    }

    private List<BookingDto> toFullBookingsDto(Long userId, List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(userService.findById(booking.getBooker().getId())),
                        itemService.findById(userId, booking.getItem().getId())))
                .collect(Collectors.toList());
    }
}

package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl service;
    @Mock
    private BookingRepository repository;
    @Mock
    private ItemServiceImpl itemService;
    @Mock
    private UserServiceImpl userService;
    private User owner;
    private User booker;
    private Item item;
    private BookingRequestDto bookingRequestDto;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "ownerName", "owner@email.ru");
        booker = new User(2L, "bookerName", "booker@email.ru");
        item = new Item(1L, "itemName", "description", true, owner.getId(), null, null, null, null);
        bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        booking = new Booking(1L, bookingRequestDto.getStart(), bookingRequestDto.getEnd(), item, booker, Status.WAITING);
        bookingDto = new BookingDto(1L, booking.getStart(), booking.getEnd(), booking.getStatus(), new BookingDto.User(booker.getId()), new BookingDto.Item(item.getId(), item.getName()));
    }

    @Test
    void save_thenSavedBookingDtoReturned() {
        when(itemService.getByIdWithCheck(anyLong())).thenReturn(item);
        when(userService.getByIdWithCheck(anyLong())).thenReturn(booker);
        when(repository.save(any())).thenReturn(booking);

        BookingDto actualBookingDto = service.save(booker.getId(), bookingRequestDto);

        assertEquals(bookingDto, actualBookingDto);
    }

    @Test
    void save_whenDateError_thenBadRequestExceptionThrown() {
        bookingRequestDto.setEnd(LocalDateTime.now());
        assertThrows(BadRequestException.class, () -> service.save(booker.getId(), bookingRequestDto));
    }

    @Test
    void save_whenUserEqualOwner_thenNotFoundExceptionThrown() {
        when(itemService.getByIdWithCheck(anyLong())).thenReturn(item);
        assertThrows(NotFoundException.class, () -> service.save(owner.getId(), bookingRequestDto));
    }

    @Test
    void save_whenNotAvailable_thenBadRequestExceptionThrown() {
        when(itemService.getByIdWithCheck(anyLong())).thenReturn(item);
        item.setAvailable(false);
        assertThrows(BadRequestException.class, () -> service.save(booker.getId(), bookingRequestDto));
    }

    @Test
    void getById_thenFoundBookingDtoReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(booker);
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto actualBookingDto = service.getById(booker.getId(), booking.getId());

        assertEquals(bookingDto, actualBookingDto);
    }

    @Test
    void getById_thenNotFoundExceptionThrown() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(booker);
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> service.getById(3L, 1L));
    }

    @Test
    void getAllByBookerId_thenBookingDtoListReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(booker);

        List<Booking> bookings = List.of(booking);

        when(repository.findAllByBookerIdOrderByStartDesc(anyLong())).thenReturn(bookings);
        when(repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookings);
        when(repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        when(repository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        when(repository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(anyLong(), any())).thenReturn(bookings);

        assertEquals(1, service.getAllByBookerId(booking.getBooker().getId(), "ALL", 0, 10).size());
        assertEquals(1, service.getAllByBookerId(booking.getBooker().getId(), "CURRENT", 0, 10).size());
        assertEquals(1, service.getAllByBookerId(booking.getBooker().getId(), "PAST", 0, 10).size());
        assertEquals(1, service.getAllByBookerId(booking.getBooker().getId(), "FUTURE", 0, 10).size());
        assertEquals(1, service.getAllByBookerId(booking.getBooker().getId(), "WAITING", 0, 10).size());
    }

    @Test
    void getAllByOwnerId_thenBookingDtoListReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);

        List<Booking> bookings = List.of(booking);

        when(repository.findAllByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(anyLong(), any())).thenReturn(bookings);

        assertEquals(1, service.getAllByOwnerId(booking.getItem().getOwnerId(), "ALL", 0, 10).size());
        assertEquals(1, service.getAllByOwnerId(booking.getItem().getOwnerId(), "CURRENT", 0, 10).size());
        assertEquals(1, service.getAllByOwnerId(booking.getItem().getOwnerId(), "PAST", 0, 10).size());
        assertEquals(1, service.getAllByOwnerId(booking.getItem().getOwnerId(), "FUTURE", 0, 10).size());
        assertEquals(1, service.getAllByOwnerId(booking.getItem().getOwnerId(), "WAITING", 0, 10).size());
    }

   @Test
    void approve_thenStatusApproved() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto approvedBookingDto = service.approve(owner.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, approvedBookingDto.getStatus());
    }

    @Test
    void approve_thenNotFoundExceptionThrown() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(booker);
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> service.approve(booker.getId(), booking.getId(), true));
    }

    @Test
    void approve_thenBadRequestExceptionThrown() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(Status.APPROVED);

        assertThrows(BadRequestException.class, () -> service.approve(owner.getId(), booking.getId(), true));
    }
}
package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@SpringBootTest
class BookingServiceImplTest {
    BookingService service;
    @Mock
    BookingRepository repository;
    @Mock
    ItemServiceImpl itemService;
    @Mock
    UserServiceImpl userService;
    User user;
    Item item;
    BookingRequestDto bookingRequestDto;
    Booking booking;

    @BeforeEach
    public void setup() {
        service = new BookingServiceImpl(repository, itemService, userService);
        user = new User(2L, "userName", "user@email.ru");
        item = new Item(1L, "itemName", "itemDescription", true, 1L, null, null, null, null);
        bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        booking = new Booking(1L, bookingRequestDto.getStart(), bookingRequestDto.getEnd(), item, user, Status.WAITING);
    }

    @Test
    void save() {
        when(itemService.getByIdWithCheck(anyLong())).thenReturn(item);
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(booking);

        BookingDto savedBookingDto = service.save(2L, bookingRequestDto);

        assertThat(savedBookingDto.getId()).isEqualTo(1L);
        assertThat(savedBookingDto.getStart()).isNotNull();
        assertThat(savedBookingDto.getEnd()).isNotNull();
        assertThat(savedBookingDto.getStatus()).isEqualTo(Status.WAITING);
        assertThat(savedBookingDto.getBooker().getId()).isEqualTo(2L);
        assertThat(savedBookingDto.getItem().getId()).isEqualTo(item.getId());

        assertThrows(NotFoundException.class, () -> service.save(1L, bookingRequestDto));

        item.setAvailable(false);
        assertThrows(BadRequestException.class, () -> service.save(2L, bookingRequestDto));

        bookingRequestDto.setEnd(LocalDateTime.now());
        assertThrows(BadRequestException.class, () -> service.save(2L, bookingRequestDto));
    }


    @Test
    void approve() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto approvedBookingDto = service.approve(1L, booking.getId(), true);

        assertThat(approvedBookingDto.getStatus()).isEqualTo(Status.APPROVED);

        assertThrows(NotFoundException.class, () -> service.approve(2L, booking.getId(), true));

        booking.setStatus(Status.APPROVED);
        assertThrows(BadRequestException.class, () -> service.approve(1L, booking.getId(), true));
    }

    @Test
    void findById() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto foundBookingDto = service.getById(1L, booking.getId());

        assertThat(foundBookingDto.getId()).isEqualTo(1L);
        assertThat(foundBookingDto.getStart()).isNotNull();
        assertThat(foundBookingDto.getEnd()).isNotNull();
        assertThat(foundBookingDto.getStatus()).isEqualTo(Status.WAITING);
        assertThat(foundBookingDto.getBooker().getId()).isEqualTo(2L);
        assertThat(foundBookingDto.getItem().getId()).isEqualTo(item.getId());

        assertThrows(NotFoundException.class, () -> service.getById(3L, 4L));
    }

    @Test
    void findAllByBookerId() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);

        List<Booking> bookings = List.of(booking);

        when(repository.findAllByBookerIdOrderByStartDesc(anyLong())).thenReturn(bookings);
        when(repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookings);
        when(repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        when(repository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        when(repository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(anyLong(), any())).thenReturn(bookings);


        assertThat(service.getAllByBookerId(booking.getBooker().getId(), "ALL", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByBookerId(booking.getBooker().getId(), "CURRENT", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByBookerId(booking.getBooker().getId(), "PAST", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByBookerId(booking.getBooker().getId(), "FUTURE", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByBookerId(booking.getBooker().getId(), "WAITING", 0, 20).size()).isEqualTo(1);
    }

    @Test
    void findAllByOwnerId() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);

        List<Booking> bookings = List.of(booking);

        when(repository.findAllByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(anyLong(), any())).thenReturn(bookings);

        assertThat(service.getAllByOwnerId(booking.getItem().getOwnerId(), "ALL", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByOwnerId(booking.getItem().getOwnerId(), "CURRENT", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByOwnerId(booking.getItem().getOwnerId(), "PAST", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByOwnerId(booking.getItem().getOwnerId(), "FUTURE", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByOwnerId(booking.getItem().getOwnerId(), "WAITING", 0, 20).size()).isEqualTo(1);
    }
}
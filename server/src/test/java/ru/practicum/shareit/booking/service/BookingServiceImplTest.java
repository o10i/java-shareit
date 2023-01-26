package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
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
    BookItemRequestDto bookItemRequestDto;
    Booking booking;

    @BeforeEach
    public void setup() {
        service = new BookingServiceImpl(repository, itemService, userService);
        user = new User(2L, "userName", "user@email.ru");
        item = new Item(1L, "itemName", "itemDescription", true, 1L, null, null, null, null);
        bookItemRequestDto = new BookItemRequestDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        booking = new Booking(1L, bookItemRequestDto.getStart(), bookItemRequestDto.getEnd(), item, user, Status.WAITING);
    }

    @Test
    void save() {
        when(itemService.findByIdWithCheck(anyLong()))
                .thenReturn(item);
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.save(any()))
                .thenReturn(booking);

        BookingDto savedBookingDto = service.add(2L, bookItemRequestDto);

        assertThat(savedBookingDto.getId()).isEqualTo(1L);
        assertThat(savedBookingDto.getStart()).isNotNull();
        assertThat(savedBookingDto.getEnd()).isNotNull();
        assertThat(savedBookingDto.getStatus()).isEqualTo(Status.WAITING);
        assertThat(savedBookingDto.getBooker().getId()).isEqualTo(2L);
        assertThat(savedBookingDto.getItem().getId()).isEqualTo(item.getId());

        assertThrows(NotFoundException.class, () -> service.add(1L, bookItemRequestDto));

        item.setAvailable(false);
        assertThrows(BadRequestException.class, () -> service.add(2L, bookItemRequestDto));

        bookItemRequestDto.setEnd(LocalDateTime.now());
        assertThrows(BadRequestException.class, () -> service.add(2L, bookItemRequestDto));
    }


    @Test
    void approve() {
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto approvedBookingDto = service.approve(1L, booking.getId(), true);

        assertThat(approvedBookingDto.getStatus()).isEqualTo(Status.APPROVED);

        assertThrows(NotFoundException.class, () -> service.approve(2L, booking.getId(), true));

        booking.setStatus(Status.APPROVED);
        assertThrows(BadRequestException.class, () -> service.approve(1L, booking.getId(), true));
    }

    @Test
    void findById() {
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto foundBookingDto = service.getById(1L, booking.getId());

        assertThat(foundBookingDto.getId()).isEqualTo(1L);
        assertThat(foundBookingDto.getStart()).isNotNull();
        assertThat(foundBookingDto.getEnd()).isNotNull();
        assertThat(foundBookingDto.getStatus()).isEqualTo(Status.WAITING);
        assertThat(foundBookingDto.getBooker().getId()).isEqualTo(2L);
        assertThat(foundBookingDto.getItem().getId()).isEqualTo(item.getId());

        assertThrows(NotFoundException.class, () -> service.getById(3L, 4L));
    }

/*    @Test
    void findAllByBookerId() {
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);

        Page<Booking> bookings = new PageImpl<>(List.of(booking));

        when(repository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookings);
        when(repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(bookings);
        when(repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(repository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(repository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);


        assertThat(service.getAllByBooker(booking.getBooker().getId(), "ALL", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByBooker(booking.getBooker().getId(), "CURRENT", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByBooker(booking.getBooker().getId(), "PAST", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByBooker(booking.getBooker().getId(), "FUTURE", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByBooker(booking.getBooker().getId(), "WAITING", 0, 20).size()).isEqualTo(1);
        assertThrows(BadRequestException.class, () -> service.getAllByBooker(booking.getBooker().getId(), "FAIL", 0, 20));
    }

    @Test
    void findAllByOwnerId() {
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);

        Page<Booking> bookings = new PageImpl<>(List.of(booking));

        when(repository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(repository.findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);

        assertThat(service.getAllByOwner(booking.getItem().getOwnerId(), "ALL", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByOwner(booking.getItem().getOwnerId(), "CURRENT", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByOwner(booking.getItem().getOwnerId(), "PAST", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByOwner(booking.getItem().getOwnerId(), "FUTURE", 0, 20).size()).isEqualTo(1);
        assertThat(service.getAllByOwner(booking.getItem().getOwnerId(), "WAITING", 0, 20).size()).isEqualTo(1);
        assertThrows(BadRequestException.class, () -> service.getAllByOwner(booking.getItem().getOwnerId(), "FAIL", 0, 20));
    }*/
}
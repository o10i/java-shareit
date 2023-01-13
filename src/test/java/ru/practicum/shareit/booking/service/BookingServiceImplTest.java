package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingRepository repository;
    BookingService service;
    @Mock
    ItemServiceImpl itemService;
    @Mock
    UserServiceImpl userService;
    BookingShortDto bookingShortDto;
    BookingDto bookingDto;

    @BeforeEach
    public void setup() {
        service = new BookingServiceImpl(repository, itemService, userService);

    }

    @Test
    void save() {
    }

    @Test
    void approve() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAllByBookerId() {
    }

    @Test
    void findAllByOwnerId() {
    }
}
package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectUnavailableException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserServiceImpl;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    BookingRepository repository;
    ItemServiceImpl itemService;
    UserServiceImpl userService;
    @Override
    public BookingDto save(Long userId, BookingDto bookingDto) {
        Long itemId = bookingDto.getItemId();
        ItemDto item = itemService.findById(itemId);

        if (!item.getAvailable()) {
            throw new ObjectUnavailableException(String.format("Item with id=%d unavailable.", itemId));
        }

        UserDto booker = userService.findById(userId);

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBookerId(userId);
        booking.setStatus(Status.WAITING);
        repository.save(booking);

        BookingDto savedBookingDto = BookingMapper.toBookingDto(booking);
        savedBookingDto.setBooker(booker);
        savedBookingDto.setItem(item);
        return savedBookingDto;
    }
}

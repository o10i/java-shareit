package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLastOrNextDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingDto toBookingDto(Booking booking, UserDto bookerDto, ItemDto itemDto) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(bookerDto);
        bookingDto.setItem(itemDto);
        return bookingDto;
    }

    public static BookingLastOrNextDto toBookingLastOrNextDto(Booking booking) {
        return new BookingLastOrNextDto(booking.getId(), booking.getBooker().getId());
    }

    public static Booking toBooking(BookingSaveDto bookingSaveDto) {
        Booking booking = new Booking();
        booking.setStart(bookingSaveDto.getStart());
        booking.setEnd(bookingSaveDto.getEnd());
        return booking;
    }
}

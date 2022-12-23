package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BookingMapper {
    public static BookingOutDto toBookingOutDto(Booking booking, UserDto booker, ItemDto item) {
        BookingOutDto bookingOutDto = new BookingOutDto();
        bookingOutDto.setId(booking.getId());
        bookingOutDto.setStart(booking.getStart());
        bookingOutDto.setEnd(booking.getEnd());
        bookingOutDto.setStatus(booking.getStatus());
        bookingOutDto.setBooker(booker);
        bookingOutDto.setItem(item);
        return bookingOutDto;
    }

    public static Booking toBooking(BookingInDto bookingInDto) {
        Booking booking = new Booking();
        booking.setItemId(bookingInDto.getItemId());
        booking.setStart(bookingInDto.getStart());
        booking.setEnd(bookingInDto.getEnd());
        return booking;
    }
}

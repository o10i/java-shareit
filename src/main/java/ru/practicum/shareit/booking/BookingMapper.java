package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class BookingMapper {
    public static BookingOutDto toBookingOutDto(Booking booking) {
        return new BookingOutDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                null,
                null
                );
    }

    public static Booking toBooking(BookingInDto bookingInDto) {
        Booking booking = new Booking();
        booking.setItemId(bookingInDto.getItemId());
        booking.setStart(bookingInDto.getStart());
        booking.setEnd(bookingInDto.getEnd());
        return booking;
    }
}

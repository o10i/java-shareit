package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingLastOrNextDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new BookingDto.User(booking.getBooker().getId(), booking.getBooker().getName()))
                .item(new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()))
                .build();
    }

    public static BookingLastOrNextDto toBookingLastOrNextDto(Booking booking) {
        return new BookingLastOrNextDto(booking.getId(), booking.getBooker().getId());
    }

    public static Booking toBooking(BookingInDto bookingInDto) {
        Booking booking = new Booking();
        booking.setStart(bookingInDto.getStart());
        booking.setEnd(bookingInDto.getEnd());
        return booking;
    }

    public static List<BookingDto> toListBookingDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}

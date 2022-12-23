package ru.practicum.shareit.booking;

public interface BookingService {
    BookingDto save(Long userId, BookingDto bookingDto);
    
//    List<Booking> findByBookerAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);
}

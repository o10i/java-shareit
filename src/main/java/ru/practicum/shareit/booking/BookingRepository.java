package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStatusEqualsOrderByStartDesc(Long userId, Status status, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStatusEqualsOrderByStartDesc(Long ownerId, Status status, Pageable pageable);

    Booking findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime dateTime);

    Booking findFirstByItem_IdAndStartAfterOrderByStart(Long itemId, LocalDateTime dateTime);
}

package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long ownerId, Status status, Pageable pageable);

    Booking findFirstByItemIdAndEndBeforeAndStatusEqualsOrderByEndDesc(Long itemId, LocalDateTime dateTime, Status status);

    List<Booking> findAllByItemInAndEndBeforeAndStatusEqualsOrderByEndDesc(List<Item> items, LocalDateTime dateTime, Status status);

    Booking findFirstByItemIdAndStartAfterAndStatusEqualsOrderByStart(Long itemId, LocalDateTime dateTime, Status status);

    List<Booking> findAllByItemInAndStartAfterAndStatusEqualsOrderByStart(List<Item> items, LocalDateTime dateTime, Status status);

    List<Booking> findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(Long bookerId, Long itemId, LocalDateTime time, Status status);
}

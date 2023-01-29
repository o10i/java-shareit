package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long userId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long userId, Status status);

    Booking findFirstByItemIdAndEndBeforeAndStatusEqualsOrderByEndDesc(Long itemId, LocalDateTime dateTime, Status status);

    List<Booking> findAllByItemInAndEndBeforeAndStatusEqualsOrderByEndDesc(List<Item> items, LocalDateTime dateTime, Status status);

    Booking findFirstByItemIdAndStartAfterAndStatusEqualsOrderByStart(Long itemId, LocalDateTime dateTime, Status status);

    List<Booking> findAllByItemInAndStartAfterAndStatusEqualsOrderByStart(List<Item> items, LocalDateTime dateTime, Status status);

    List<Booking> findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(Long userId, Long itemId, LocalDateTime time, Status status);
}

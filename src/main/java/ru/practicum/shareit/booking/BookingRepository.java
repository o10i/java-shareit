package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long userId, Status status, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerOrderByStartDesc(Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?3 " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerAndStatusEqualsOrderByStartDesc(Long userId, Status status, Pageable pageable);


    @Query(value = "select * " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.id = ?1 " +
            "and b.end_date < ?2 " +
            "order by b.end_date desc " +
            "limit 1",
            nativeQuery = true)
    Optional<Booking> findLastBookingByItemId(Long itemId, LocalDateTime dateTime);

    @Query(value = "select * " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.id = ?1 " +
            "and b.start_date > ?2 " +
            "order by b.start_date " +
            "limit 1",
            nativeQuery = true)
    Optional<Booking> findNextBookingByItemId(Long itemId, LocalDateTime dateTime);
}

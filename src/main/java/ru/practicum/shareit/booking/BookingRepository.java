package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long userId, Status status);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerOrderByStartDesc(Long userId);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 and " +
            "b.start > ?2 and " +
            "b.end < ?3 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 and " +
            "b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 and " +
            "b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerAndStartAfterOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.ownerId = ?1 and " +
            "b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerAndStatusEqualsOrderByStartDesc(Long userId, Status status);
}

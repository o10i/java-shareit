package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.Booking;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "items")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String name;
    @Column
    String description;
    @Column(name = "is_available")
    Boolean available;
    @Column(name = "owner_id")
    Long ownerId;
    @Column(name = "request_id")
    Long requestId;
    @Transient
    Booking lastBooking;
    @Transient
    Booking nextBooking;
    @Transient
    Set<Comment> comments;
}

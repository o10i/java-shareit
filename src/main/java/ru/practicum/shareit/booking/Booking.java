package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "bookings")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "start_date")
    String start;
    @Column(name = "end_date")
    String end;
    @Column(name = "item_id")
    Long item;
    @Column(name = "booker_id")
    Long booker;
    @Enumerated(EnumType.STRING)
    Status status;
}

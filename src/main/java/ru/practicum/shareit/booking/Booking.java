package ru.practicum.shareit.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Booking {
    Long id;
    String start;
    String end;
    Long item;
    Long booker;
    Status status;
}

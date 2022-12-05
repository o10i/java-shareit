package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    String start;
    String end;
    Long item;
    Long booker;
    Status status;
}

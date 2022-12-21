package ru.practicum.shareit.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    String start;
    String end;
    Long item;
    Long booker;
    Status status;
}

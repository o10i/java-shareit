package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Status status;
    User booker;
    Item item;

    @AllArgsConstructor
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class User {
        Long id;
    }

    @AllArgsConstructor
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Item {
        Long id;
        String name;
    }
}

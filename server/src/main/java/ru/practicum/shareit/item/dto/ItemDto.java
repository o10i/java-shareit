package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDto lastBooking;
    BookingDto nextBooking;
    Set<CommentDto> comments;

    @AllArgsConstructor
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class BookingDto {
        Long id;
        Long bookerId;
    }
}

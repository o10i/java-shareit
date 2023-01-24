package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDto lastBooking;
    BookingDto nextBooking;
    Set<CommentDto> comments;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class BookingDto {
        Long id;
        Long bookerId;
    }
}

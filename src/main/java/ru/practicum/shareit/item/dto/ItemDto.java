package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
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

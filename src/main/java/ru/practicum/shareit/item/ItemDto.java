package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    Long id;
    String name;
    String description;
    boolean available;
    Long owner;
    Long request;
}

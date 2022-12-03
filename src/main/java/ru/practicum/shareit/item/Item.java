package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    boolean available;
    Long owner;
    ItemRequest request;
}

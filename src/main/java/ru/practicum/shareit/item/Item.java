package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class Item {
    Long id;
    String name;
    String description;
    boolean available;
    Long owner;
    ItemRequest request;
}

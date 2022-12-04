package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item create(Long owner, Item item);

    Item update(Long itemId, Item item);

    Item getById(Long itemId);

    List<Item> getAll(Long owner);

    void deleteById(Long itemId);
}

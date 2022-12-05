package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item create(Long userId, Item item);

    Item update(Long itemId, Item item);

    Item getById(Long itemId);

    List<Item> getAll(Long userId);

    void deleteById(Long itemId);

    List<Item> search(String text);
}

package ru.practicum.shareit.item;

public interface ItemRepository {
    Item create(Long owner, Item item);

}

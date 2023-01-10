package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

interface ItemService {
    Item save(Item item);

    Item update(Long itemId, Item item);

    Item findById(Long userId, Long itemId);

    List<Item> findAllByOwnerId(Long userId, Integer from, Integer size);

    void deleteById(Long itemId);

    List<Item> search(String text, Integer from, Integer size);

    Comment saveComment(Long userId, Long itemId, Comment comment);
}

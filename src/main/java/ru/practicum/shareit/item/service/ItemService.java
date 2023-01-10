package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemShortDto save(Long ownerId, ItemShortDto itemShortDto);

    ItemShortDto update(Long itemId, Long ownerId, ItemShortDto itemShortDto);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findAllByOwnerId(Long userId, Integer from, Integer size);

    void deleteById(Long itemId);

    List<ItemShortDto> search(String text, Integer from, Integer size);

    CommentDto saveComment(Long userId, Long itemId, Comment comment);
}

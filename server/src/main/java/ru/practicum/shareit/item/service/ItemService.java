package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemRequestDto save(Long ownerId, ItemRequestDto itemRequestDto);

    ItemRequestDto update(Long userId, Long itemId, ItemRequestDto itemRequestDto);

    ItemDto getById(Long userId, Long itemId);

    List<ItemDto> getAllByOwnerId(Long ownerId, Integer from, Integer size);

    List<ItemRequestDto> search(String text, Integer from, Integer size);

    CommentDto saveComment(Long authorId, Long itemId, Comment comment);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemBookingDto update(Long userId, Long itemId, ItemBookingDto itemBookingDto);

    ItemBookingDto findById(Long userId, Long itemId);

    List<ItemBookingDto> findAllByOwnerId(Long userId, Integer from, Integer size);

    void deleteById(Long itemId);

    List<ItemBookingDto> search(String text, Integer from, Integer size);

    CommentDto saveComment(Long userId, Long itemId, CommentDto commentDto);
}

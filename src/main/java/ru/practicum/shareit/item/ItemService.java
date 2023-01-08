package ru.practicum.shareit.item;

import java.util.List;

interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findAllByOwnerId(Long userId, Integer from, Integer size);

    void deleteById(Long itemId);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto saveComment(Long userId, Long itemId, CommentDto commentDto);
}

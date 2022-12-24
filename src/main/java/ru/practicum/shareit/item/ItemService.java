package ru.practicum.shareit.item;

import java.util.List;

interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findAllByOwnerId(Long userId);

    void deleteById(Long itemId);

    List<ItemDto> search(String text);

    CommentDto saveComment(Long userId, Long itemId, CommentDto commentDto);
}

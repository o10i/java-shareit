package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;

    public ItemDto create(ItemDto itemDto) {
        Item item = repository.create(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }
}

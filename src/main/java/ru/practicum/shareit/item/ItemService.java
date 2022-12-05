package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotEqualException;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemService {
    ItemRepository repository;
    UserService userService;

    public ItemDto create(Long userId, ItemDto itemDto) {
        userService.getById(userId);
        Item item = repository.create(userId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Long owner = repository.getById(itemId).getOwner();
        if (!Objects.equals(userId, owner)) {
            throw new ObjectNotEqualException(String.format("userId=%d and owner=%d are not equal", userId, owner));
        }
        Item item = repository.update(itemId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getById(Long itemId) {
        Item item = repository.getById(itemId);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getAll(Long userId) {
        userService.getById(userId);
        List<Item> items = repository.getAll(userId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
    }

    public List<ItemDto> search(String text) {
        List<Item> items = new ArrayList<>();
        if (!text.equals("")) {
            items = repository.search(text);
        }
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}

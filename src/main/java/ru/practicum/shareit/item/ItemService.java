package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotEqualException;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemService {
    ItemRepository repository;
    UserService userService;

    public ItemDto create(Long owner, ItemDto itemDto) {
        userService.getById(owner);
        Item item = repository.create(owner, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto update(Long owner, Long itemId, ItemDto itemDto) {
        Long itemOwner = repository.getById(itemId).getOwner();
        if (!Objects.equals(owner, itemOwner)) {
            throw new ObjectNotEqualException(String.format("Owner=%d and itemOwner=%d are not equal", owner, itemOwner));
        }
        Item item = repository.update(itemId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getById(Long itemId) {
        Item item = repository.getById(itemId);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getAll(Long owner) {
        userService.getById(owner);
        List<Item> items = repository.getAll(owner);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
    }
}

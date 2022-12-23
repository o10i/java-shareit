package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotEqualException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository repository;
    UserServiceImpl userService;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Item with id=%d not found", itemId)));
        if (!userId.equals(item.getOwnerId())) {
            throw new ObjectNotEqualException(String.format("userId=%d and owner=%d are not equal", userId, item.getOwnerId()));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(Long itemId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Item with id=%d not found", itemId)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllByOwnerId(Long userId) {
        userService.findById(userId);
        return toItemsDto(repository.findAllByOwnerId(userId));
    }

    @Override
    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (!text.equals("")) {
            return repository.search(text);
        }
        return new ArrayList<>();
    }

    public Long findItemOwnerIdById(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Item with id=%d not found", itemId)))
                .getOwnerId();
    }

    private List<ItemDto> toItemsDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
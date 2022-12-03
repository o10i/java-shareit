package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserService;

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
}

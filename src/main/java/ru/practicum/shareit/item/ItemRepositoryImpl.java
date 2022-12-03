package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRepositoryImpl implements ItemRepository {
    final List<Item> items = new ArrayList<>();
    Long idCounter = 1L;

    @Override
    public Item create(Long owner, Item item) {
        item.setId(idCounter++);
        item.setOwner(owner);
        items.add(item);
        return item;
    }
}

package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Item update(Long itemId, Item item) {
        Item updatedItem = getById(itemId);
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return updatedItem;
    }

    @Override
    public Item getById(Long itemId) {
        return items.stream().filter(item -> item.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Item with id=%d not found", itemId)));
    }

    @Override
    public List<Item> getAll(Long owner) {
        return items.stream().filter(item -> item.getOwner().equals(owner)).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long itemId) {
        items.remove(getById(itemId));
    }
}

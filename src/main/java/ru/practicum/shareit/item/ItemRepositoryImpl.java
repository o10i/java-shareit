package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRepositoryImpl implements ItemRepository {
    final Map<Long, Item> items = new HashMap<>();
    final Map<Long, List<Item>> usersItems = new LinkedHashMap<>();
    Long idCounter = 1L;

    @Override
    public Item create(Long userId, Item item) {
        item.setId(idCounter++);
        item.setOwner(userId);

        items.put(item.getId(), item);

        List<Item> userItems = usersItems.computeIfAbsent(userId, aLong -> new ArrayList<>());
        userItems.add(item);
        usersItems.put(userId, userItems);

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
        return Optional.ofNullable(items.get(itemId))
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Item with id=%d not found", itemId)));
    }

    @Override
    public List<Item> getAll(Long userId) {
        return usersItems.get(userId);
    }

    @Override
    public void deleteById(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> search(String text) {
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(lowerCaseText) ||
                item.getDescription().toLowerCase().contains(lowerCaseText))).collect(Collectors.toList());
    }
}

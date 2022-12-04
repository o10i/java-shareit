package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping()
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long owner,
                          @Valid @RequestBody ItemDto itemDto) {
        return service.create(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long owner,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return service.update(owner, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        return service.getById(itemId);
    }

    @GetMapping()
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long owner) {
        return service.getAll(owner);
    }
    
    @DeleteMapping("{itemId}")
    public void deleteById(@PathVariable Long itemId) {
        service.deleteById(itemId);
    }
}

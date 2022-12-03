package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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


}

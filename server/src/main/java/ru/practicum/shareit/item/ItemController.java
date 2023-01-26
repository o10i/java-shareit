package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping()
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemRequestDto itemRequestDto) {
        return service.add(userId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        return service.getById(userId, itemId);
    }

    @GetMapping()
    public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllByOwner(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemRequestDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody ItemRequestDto requestDto) {
        return service.update(userId, itemId, requestDto);
    }

    @GetMapping("/search")
    public List<ItemRequestDto> search(@RequestParam String text,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return service.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, toComment(commentDto));
    }
}
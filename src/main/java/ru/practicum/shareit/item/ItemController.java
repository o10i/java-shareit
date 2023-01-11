package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping()
    public ItemShortDto save(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @Valid @RequestBody ItemShortDto itemShortDto) {
        return service.save(ownerId, itemShortDto);
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @PathVariable Long itemId,
                               @RequestBody ItemShortDto itemShortDto) {
        return service.update(itemId, ownerId, itemShortDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId) {
        return service.findById(userId, itemId);
    }

    @GetMapping()
    public List<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return service.findAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemShortDto> search(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                     @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return service.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody CommentDto commentDto) {
        return service.saveComment(authorId, itemId, toComment(commentDto));
    }
}

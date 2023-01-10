package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl service;

    @PostMapping()
    public ItemShortDto save(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @Valid @RequestBody ItemShortDto itemShortDto) {
        return toItemSaveDto(service.save(toItem(itemShortDto, ownerId)));
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @PathVariable Long itemId,
                               @RequestBody ItemShortDto itemShortDto) {
        return toItemSaveDto(service.update(itemId, toItem(itemShortDto, ownerId)));
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId) {
        return toItemDto(service.findById(userId, itemId));
    }

    @GetMapping()
    public List<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return toListItemDto(service.findAllByOwnerId(userId, from, size));
    }

    @DeleteMapping("{itemId}")
    public void deleteById(@PathVariable Long itemId) {
        service.deleteById(itemId);
    }

    @GetMapping("/search")
    public List<ItemShortDto> search(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                     @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return toListItemSaveDto(service.search(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody CommentDto commentDto) {
        return toCommentDto(service.saveComment(authorId, itemId, toComment(commentDto)));
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Create item {}, userId={}", requestDto, userId);
        return itemClient.add(userId, requestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        log.info("Get itemId={}, userId={}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items by owner, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllByOwner(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId,
                                         @RequestBody ItemRequestDto requestDto) {
        log.info("Update item {} with itemId={}, userId={}", requestDto, itemId, userId);
        return itemClient.update(userId, itemId, requestDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items by tag={}, from={}, size={}", text, from, size);
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Create comment, itemId={}, userId={}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
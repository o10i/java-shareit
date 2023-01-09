package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl service;

    @PostMapping()
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @Valid @RequestBody ItemDto itemDto) {
        return service.save(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemBookingDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody ItemBookingDto itemBookingDto) {
        return service.update(userId, itemId, itemBookingDto);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        return service.findById(userId, itemId);
    }

    @GetMapping()
    public List<ItemBookingDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return service.findAllByOwnerId(userId, from, size);
    }

    @DeleteMapping("{itemId}")
    public void deleteById(@PathVariable Long itemId) {
        service.deleteById(itemId);
    }

    @GetMapping("/search")
    public List<ItemBookingDto> search(@RequestParam String text,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return service.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody CommentDto commentDto) {
        return service.saveComment(userId, itemId, commentDto);
    }
}

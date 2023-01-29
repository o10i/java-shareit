package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemRequestDto toItemShortDto(Item item) {
        return new ItemRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId());
    }

    public static List<ItemRequestDto> toListItemShortDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemShortDto).collect(Collectors.toList());
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getLastBooking() == null ? null :
                        new ItemDto.BookingDto(
                                item.getLastBooking().getId(),
                                item.getLastBooking().getBooker().getId()),
                item.getNextBooking() == null ? null :
                        new ItemDto.BookingDto(
                                item.getNextBooking().getId(),
                                item.getNextBooking().getBooker().getId()),
                item.getComments() == null ? null : CommentMapper.toListCommentDto(item.getComments()));
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public static Item toItem(ItemRequestDto itemRequestDto, Long ownerId) {
        Item item = new Item();
        item.setName(itemRequestDto.getName());
        item.setDescription(itemRequestDto.getDescription());
        item.setAvailable(itemRequestDto.getAvailable());
        item.setRequestId(itemRequestDto.getRequestId());
        item.setOwnerId(ownerId);
        return item;
    }
}

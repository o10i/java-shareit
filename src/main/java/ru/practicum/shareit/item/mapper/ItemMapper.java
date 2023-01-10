package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemShortDto toItemSaveDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public static List<ItemShortDto> toListItemSaveDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemSaveDto).collect(Collectors.toList());
    }


    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(item.getLastBooking() == null ? null :
                        new ItemDto.BookingDto(
                                item.getLastBooking().getId(),
                                item.getLastBooking().getBooker().getId()))
                .nextBooking(item.getNextBooking() == null ? null :
                        new ItemDto.BookingDto(
                                item.getNextBooking().getId(),
                                item.getNextBooking().getBooker().getId()))
                .comments(item.getComments() == null ? null : CommentMapper.toListCommentDto(item.getComments()))
                .build();
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public static Item toItem(ItemShortDto itemShortDto, Long ownerId) {
        Item item = new Item();
        item.setName(itemShortDto.getName());
        item.setDescription(itemShortDto.getDescription());
        item.setAvailable(itemShortDto.getAvailable());
        item.setRequestId(itemShortDto.getRequestId());
        item.setOwnerId(ownerId);
        return item;
    }
}

package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository repository;
    BookingRepository bookingRepository;
    UserServiceImpl userService;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
        if (!userId.equals(item.getOwnerId())) {
            throw new ForbiddenException(String.format("userId=%d and owner=%d are not equal", userId, item.getOwnerId()));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (userId.equals(item.getOwnerId())) {
            setBookingsToItemDto(itemDto);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> findAllByOwnerId(Long userId) {
        userService.findById(userId);
        List<ItemDto> itemsDto = toItemsDto(repository.findAllByOwnerIdOrderById(userId));
        itemsDto.forEach(this::setBookingsToItemDto);
        return itemsDto;
    }

    @Override
    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (!text.equals("")) {
            return repository.search(text);
        }
        return new ArrayList<>();
    }

    public Long findItemOwnerIdById(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)))
                .getOwnerId();
    }

    private List<ItemDto> toItemsDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void setBookingsToItemDto(ItemDto itemDto) {
        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId());
        itemDto.setLastBooking(bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .findFirst().orElse(null));
        itemDto.setNextBooking(bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .findFirst().orElse(null));
    }
}
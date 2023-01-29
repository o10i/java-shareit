package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository repository;
    BookingRepository bookingRepository;
    UserServiceImpl userService;
    CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemRequestDto save(Long userId, ItemRequestDto itemRequestDto) {
        userService.findByIdWithCheck(userId);
        return toItemShortDto(repository.save(toItem(itemRequestDto, userId)));
    }

    @Transactional
    @Override
    public ItemRequestDto update(Long userId, Long itemId, ItemRequestDto itemRequestDto) {
        userService.findByIdWithCheck(userId);

        Item itemToUpdate = findByIdWithCheck(itemId);

        if (!userId.equals(itemToUpdate.getOwnerId())) {
            throw new ForbiddenException(
                    String.format("userId=%d and owner=%d are not equal", userId, itemToUpdate.getOwnerId()));
        }
        if (itemRequestDto.getName() != null && !itemRequestDto.getName().isBlank()) {
            itemToUpdate.setName(itemRequestDto.getName());
        }
        if (itemRequestDto.getDescription() != null && !itemRequestDto.getDescription().isBlank()) {
            itemToUpdate.setDescription(itemRequestDto.getDescription());
        }
        if (itemRequestDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemRequestDto.getAvailable());
        }

        return toItemShortDto(itemToUpdate);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        userService.findByIdWithCheck(userId);

        Item item = findByIdWithCheck(itemId);
        item.setComments(commentRepository.findAllByItemId(itemId));

        if (userId.equals(item.getOwnerId())) {
            item.setLastBooking(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusEqualsOrderByEndDesc(
                    itemId, LocalDateTime.now(), Status.APPROVED));
            item.setNextBooking(bookingRepository.findFirstByItemIdAndStartAfterAndStatusEqualsOrderByStart(
                    itemId, LocalDateTime.now(), Status.APPROVED));
        }

        return toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwnerId(Long userId, Integer from, Integer size) {
        userService.findByIdWithCheck(userId);

        List<Item> items = repository.findAllByOwnerIdOrderById(userId)
                .stream().skip(from).limit(size).collect(Collectors.toList());

        Map<Item, Set<Comment>> comments = commentRepository.findAllByItemIn(items)
                .stream()
                .collect(groupingBy(Comment::getItem, toSet()));

        Map<Item, List<Booking>> lastBookings = bookingRepository
                .findAllByItemInAndEndBeforeAndStatusEqualsOrderByEndDesc(items, LocalDateTime.now(), Status.APPROVED)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        Map<Item, List<Booking>> nextBookings = bookingRepository
                .findAllByItemInAndStartAfterAndStatusEqualsOrderByStart(items, LocalDateTime.now(), Status.APPROVED)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        items.forEach(item -> {
            item.setComments(comments.get(item));
            item.setLastBooking(lastBookings.getOrDefault(item, List.of()).stream().findFirst().orElse(null));
            item.setNextBooking(nextBookings.getOrDefault(item, List.of()).stream().findFirst().orElse(null));
        });

        return toListItemDto(items);
    }

    @Override
    public List<ItemRequestDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return List.of();
        }
        return toListItemShortDto(repository.search(text, PageRequest.of(from / size, size)).getContent());
    }

    @Transactional
    @Override
    public CommentDto saveComment(Long userId, Long itemId, Comment comment) {
        User author = userService.findByIdWithCheck(userId);
        Item item = findByIdWithCheck(itemId);

        if (bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(
                userId, itemId, LocalDateTime.now(), Status.APPROVED).isEmpty()) {
            throw new BadRequestException(
                    String.format("userId=%d hasn't booking for itemId=%d in past.", userId, itemId));
        }

        comment.setItem(item);
        comment.setAuthor(author);

        return toCommentDto(commentRepository.save(comment));
    }

    public Item findByIdWithCheck(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
    }
}
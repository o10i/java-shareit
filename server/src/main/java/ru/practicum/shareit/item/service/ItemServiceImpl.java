package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.*;

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
    public ItemRequestDto save(Long ownerId, ItemRequestDto itemRequestDto) {
        userService.getByIdWithCheck(ownerId);
        return toItemRequestDto(repository.save(toItem(itemRequestDto, ownerId)));
    }

    @Transactional
    @Override
    public ItemRequestDto update(Long userId, Long itemId, ItemRequestDto itemRequestDto) {
        userService.getByIdWithCheck(userId);

        Item itemToUpdate = getByIdWithCheck(itemId);

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

        return toItemRequestDto(itemToUpdate);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        userService.getByIdWithCheck(userId);

        Item item = getByIdWithCheck(itemId);
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
    public List<ItemDto> getAllByOwnerId(Long ownerId, Integer from, Integer size) {
        userService.getByIdWithCheck(ownerId);

        List<Item> items = repository.findAllByOwnerIdOrderById(ownerId)
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

        return toItemDtoList(items);
    }

    @Override
    public List<ItemRequestDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return List.of();
        }
        return toItemRequestDtoList(repository.search(text).stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public CommentDto saveComment(Long authorId, Long itemId, Comment comment) {
        User author = userService.getByIdWithCheck(authorId);
        Item item = getByIdWithCheck(itemId);

        if (bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(
                authorId, itemId, LocalDateTime.now(), Status.APPROVED).isEmpty()) {
            throw new BadRequestException(
                    String.format("userId=%d hasn't booking for itemId=%d in past.", authorId, itemId));
        }

        comment.setItem(item);
        comment.setAuthor(author);

        return toCommentDto(commentRepository.save(comment));
    }

    public Item getByIdWithCheck(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
    }
}
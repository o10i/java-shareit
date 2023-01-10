package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

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
    public Item save(Item item) {
        userService.findByIdWithCheck(item.getOwnerId());
        return repository.save(item);
    }

    @Transactional
    @Override
    public Item update(Long itemId, Item item) {
        userService.findByIdWithCheck(item.getOwnerId());

        Item itemToUpdate = findByIdWithCheck(itemId);

        if (!item.getOwnerId().equals(itemToUpdate.getOwnerId())) {
            throw new ForbiddenException(String.format("userId=%d and owner=%d are not equal", item.getOwnerId(), itemToUpdate.getOwnerId()));
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }

        return itemToUpdate;
    }

    @Override
    public Item findById(Long userId, Long itemId) {
        userService.findByIdWithCheck(userId);

        Item item = findByIdWithCheck(itemId);
        item.setComments(commentRepository.findAllByItemId(itemId));

        if (userId.equals(item.getOwnerId())) {
            item.setLastBooking(bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
            item.setNextBooking(bookingRepository.findFirstByItem_IdAndStartAfterOrderByStart(itemId, LocalDateTime.now()));
        }

        return item;
    }

    @Override
    public List<Item> findAllByOwnerId(Long userId, Integer from, Integer size) {
        userService.findByIdWithCheck(userId);

        List<Item> items = repository.findAllByOwnerIdOrderById(userId, PageRequest.of(from / size, size)).getContent();

        Map<Item, Set<Comment>> comments = commentRepository.findByItemIn(items)
                .stream()
                .collect(groupingBy(Comment::getItem, toSet()));

        items.forEach(item -> {
            item.setComments(comments.get(item));
            item.setLastBooking(bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now()));
            item.setNextBooking(bookingRepository.findFirstByItem_IdAndStartAfterOrderByStart(item.getId(), LocalDateTime.now()));
        });

        return items;
    }

    @Override
    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    public List<Item> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return List.of();
        }
        return repository.search(text, PageRequest.of(from / size, size)).getContent();
    }

    @Transactional
    @Override
    public Comment saveComment(Long authorId, Long itemId, Comment comment) {
        User author = userService.findByIdWithCheck(authorId);
        Item item = findByIdWithCheck(itemId);

        List<Booking> userBookings = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(authorId, LocalDateTime.now(), PageRequest.of(0, 20)).getContent();
        userBookings.stream().filter(booking -> booking.getItem().getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new BadRequestException(String.format("userId=%d hasn't booking for itemId=%d in past.", authorId, itemId)));

        comment.setItem(item);
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    public Item findByIdWithCheck(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
    }

    public List<Item> findAllByRequestId(Long requestId) {
        return repository.findAllByRequestId(requestId);
    }
}
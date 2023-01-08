package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository repository;
    BookingRepository bookingRepository;
    UserServiceImpl userService;
    CommentRepository commentRepository;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        userService.findByIdWithException(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = findByIdWithException(itemId);

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
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        userService.findByIdWithException(userId);

        Item item = findByIdWithException(itemId);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (userId.equals(item.getOwnerId())) {
            setBookingsToItemDto(itemDto);
        }

        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (comments != null) {
            List<CommentDto> commentsDto = toFullCommentsDto(comments);
            itemDto.setComments(commentsDto);
        } else {
            itemDto.setComments(new ArrayList<>());
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> findAllByOwnerId(Long userId, Integer from, Integer size) {
        userService.findByIdWithException(userId);

        Page<Item> items = repository.findAllByOwnerIdOrderById(userId, PageRequest.of(from / size, size));
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        itemsDto.forEach(this::setBookingsToItemDto);

        return itemsDto;
    }

    @Override
    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Page<ItemDto> search = repository.search(text, PageRequest.of(from / size, size));
        return search.getContent();
    }

    @Override
    public CommentDto saveComment(Long userId, Long itemId, CommentDto commentDto) {
        userService.findByIdWithException(userId);

        List<Booking> userBookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(0, 20)).getContent();
        userBookings.stream().filter(booking -> booking.getItemId().equals(itemId)).findFirst()
                .orElseThrow(() -> new BadRequestException(String.format("userId=%d hasn't booking for itemId=%d in past.", userId, itemId)));

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        commentRepository.save(comment);
        return toFullCommentDto(comment, userService.findByIdWithException(userId).getName());
    }

    public Item findByIdWithException(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
    }

    public List<ItemDto> findAllByRequestId(Long requestId) {
        return toItemsDto(repository.findAllByRequestId(requestId));
    }

    private List<ItemDto> toItemsDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void setBookingsToItemDto(ItemDto itemDto) {
        Optional<Booking> lastBooking = bookingRepository.findLastBookingByItemId(itemDto.getId(), LocalDateTime.now());
        itemDto.setLastBooking(lastBooking.orElse(null));
        Optional<Booking> nextBooking = bookingRepository.findNextBookingByItemId(itemDto.getId(), LocalDateTime.now());
        itemDto.setNextBooking(nextBooking.orElse(null));
    }

    private CommentDto toFullCommentDto(Comment comment, String authorName) {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        commentDto.setAuthorName(authorName);
        return commentDto;
    }

    private List<CommentDto> toFullCommentsDto(List<Comment> comments) {
        return comments.stream().map(comment -> toFullCommentDto(comment,
                        userService.findByIdWithException(comment.getAuthorId()).getName()))
                .collect(Collectors.toList());
    }
}
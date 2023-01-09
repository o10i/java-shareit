package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
    CommentRepository commentRepository;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        userService.findByIdWithException(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toItemRequestDto(repository.save(item));
    }

    @Override
    public ItemBookingDto update(Long userId, Long itemId, ItemBookingDto itemBookingDto) {
        Item item = findByIdWithException(itemId);

        if (!userId.equals(item.getOwnerId())) {
            throw new ForbiddenException(String.format("userId=%d and owner=%d are not equal", userId, item.getOwnerId()));
        }
        if (itemBookingDto.getName() != null) {
            item.setName(itemBookingDto.getName());
        }
        if (itemBookingDto.getDescription() != null) {
            item.setDescription(itemBookingDto.getDescription());
        }
        if (itemBookingDto.getAvailable() != null) {
            item.setAvailable(itemBookingDto.getAvailable());
        }
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemBookingDto findById(Long userId, Long itemId) {
        userService.findByIdWithException(userId);

        Item item = findByIdWithException(itemId);

        ItemBookingDto itemBookingDto = ItemMapper.toItemDto(item);
        if (userId.equals(item.getOwnerId())) {
            setBookingsToItemDto(itemBookingDto);
        }

        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (comments != null) {
            List<CommentDto> commentsDto = toFullCommentsDto(comments);
            itemBookingDto.setComments(commentsDto);
        } else {
            itemBookingDto.setComments(new ArrayList<>());
        }
        return itemBookingDto;
    }

    @Override
    public List<ItemBookingDto> findAllByOwnerId(Long userId, Integer from, Integer size) {
        userService.findByIdWithException(userId);

        Page<Item> items = repository.findAllByOwnerIdOrderById(userId, PageRequest.of(from / size, size));
        List<ItemBookingDto> itemsDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        itemsDto.forEach(this::setBookingsToItemDto);

        return itemsDto;
    }

    @Override
    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    public List<ItemBookingDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Page<ItemBookingDto> search = repository.search(text, PageRequest.of(from / size, size));
        return search.getContent();
    }

    @Override
    public CommentDto saveComment(Long userId, Long itemId, CommentDto commentDto) {
        userService.findByIdWithException(userId);

        List<Booking> userBookings = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(0, 20)).getContent();
        userBookings.stream().filter(booking -> booking.getItem().getId().equals(itemId)).findFirst()
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
        return toItemsRequestDto(repository.findAllByRequestId(requestId));
    }

    private List<ItemDto> toItemsRequestDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemRequestDto).collect(Collectors.toList());
    }

    private void setBookingsToItemDto(ItemBookingDto itemBookingDto) {
        bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemBookingDto.getId(), LocalDateTime.now())
                .ifPresent(lastBooking -> itemBookingDto.setLastBooking(BookingMapper.toBookingLastOrNextDto(lastBooking)));
        bookingRepository.findFirstByItem_IdAndStartAfterOrderByStart(itemBookingDto.getId(), LocalDateTime.now())
                .ifPresent(nextBooking -> itemBookingDto.setNextBooking(BookingMapper.toBookingLastOrNextDto(nextBooking)));
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
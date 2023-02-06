package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl service;
    @Mock
    private ItemRepository repository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private CommentRepository commentRepository;
    private User owner;
    private User booker;
    private ItemRequestDto itemRequestDto;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "ownerName", "owner@email.ru");
        booker = new User(2L, "bookerName", "booker@email.ru");
        itemRequestDto = new ItemRequestDto(1L, "itemName", "description", true, null);
        item = new Item(1L, itemRequestDto.getName(), itemRequestDto.getDescription(), itemRequestDto.getAvailable(), owner.getId(), itemRequestDto.getRequestId(), null, null, null);
        itemDto = new ItemDto(1L, item.getName(), item.getDescription(), item.getAvailable(), new ItemDto.BookingDto(1L, booker.getId()), new ItemDto.BookingDto(1L, booker.getId()), Set.of());
        commentDto = new CommentDto(1L, "comment", owner.getName(), null);
        comment = new Comment(1L, commentDto.getText(), item, owner, commentDto.getCreated());
        booking = new Booking(1L, null, null, null, booker, null);
    }

    @Test
    void save_thenSavedItemDtoReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);
        when(repository.save(any())).thenReturn(item);

        ItemRequestDto actualItemRequestDto = service.save(booker.getId(), itemRequestDto);

        assertEquals(itemRequestDto, actualItemRequestDto);
    }

 @Test
    void getById_thenFoundItemDtoReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusEqualsOrderByEndDesc(anyLong(), any(), any())).thenReturn(booking);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusEqualsOrderByStart(anyLong(), any(), any())).thenReturn(booking);

        ItemDto actualItemDto = service.getById(owner.getId(), item.getId());

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getAllByOwnerId_thenFoundItemDtoListReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);
        when(repository.findAllByOwnerIdOrderById(anyLong())).thenReturn(List.of(item));
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setComments(null);

        List<ItemDto> actualItemDtoList = service.getAllByOwnerId(owner.getId(), 0, 10);

        assertEquals(1, actualItemDtoList.size());
        assertEquals(itemDto, actualItemDtoList.get(0));
    }

    @Test
    void update_thenUpdatedItemDtoReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        itemRequestDto.setName("nameUpdated");

        ItemRequestDto actualItemRequestDto = service.update(owner.getId(), item.getId(), itemRequestDto);

        assertEquals(itemRequestDto, actualItemRequestDto);
    }

    @Test
    void update_thenForbiddenExceptionThrown() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(ForbiddenException.class, () -> service.update(2L, 2L, itemRequestDto));
    }

    @Test
    void search_thenFoundItemRequestDtoListReturned() {
        when(repository.search(anyString())).thenReturn(List.of(item));

        List<ItemRequestDto> actualItemRequestDtoList = service.search("item", 0, 10);

        assertEquals(1, actualItemRequestDtoList.size());
        assertEquals(itemRequestDto, actualItemRequestDtoList.get(0));
    }

    @Test
    void search_thenEmptyItemRequestDtoListReturned() {
        assertEquals(0, service.search("", 0, 10).size());
    }

    @Test
    void saveComment_thenSavedCommentDtoReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(any(), any(), any(), any())).thenReturn(List.of(new Booking()));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto actualCommentDto = service.saveComment(owner.getId(), item.getId(), comment);

        assertEquals(commentDto, actualCommentDto);
    }

    @Test
    void saveComment_thenBadRequestExceptionThrown() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(owner);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(any(), any(), any(), any())).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> service.saveComment(owner.getId(), item.getId(), comment));
    }
}
package ru.practicum.shareit.item.service;

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

    @Test
    void save_thenSavedItemDtoReturned() {
        User user = getUser();
        Item item = getItem();
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(item);

        ItemRequestDto itemRequestDto = getItemRequestDto();
        ItemRequestDto actualItemRequestDto = service.save(user.getId(), itemRequestDto);

        assertEquals(itemRequestDto, actualItemRequestDto);
    }

    @Test
    void getById_thenFoundItemDtoReturned() {
        User user = getUser();
        Item item = getItem();
        Booking booking = getBooking();
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusEqualsOrderByEndDesc(anyLong(), any(), any())).thenReturn(booking);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusEqualsOrderByStart(anyLong(), any(), any())).thenReturn(booking);

        ItemDto actualItemDto = service.getById(user.getId(), item.getId());

        assertEquals(getItemDtoForOne(), actualItemDto);
    }

    @Test
    void getAllByOwnerId_thenFoundItemDtoListReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(getUser());
        when(repository.findAllByOwnerIdOrderById(anyLong())).thenReturn(List.of(getItem()));

        List<ItemDto> actualItemDtoList = service.getAllByOwnerId(1L, 0, 10);

        assertEquals(1, actualItemDtoList.size());
        assertEquals(getItemDtoForAll(), actualItemDtoList.get(0));
    }

    @Test
    void update_thenUpdatedItemDtoReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(getUser());
        when(repository.findById(anyLong())).thenReturn(Optional.of(getItem()));

        ItemRequestDto itemRequestDto = getItemRequestDto();
        itemRequestDto.setName("nameUpdated");
        ItemRequestDto actualItemRequestDto = service.update(1L, 1L, itemRequestDto);

        assertEquals(itemRequestDto, actualItemRequestDto);
    }

    @Test
    void update_thenForbiddenExceptionThrown() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(getItem()));
        assertThrows(ForbiddenException.class, () -> service.update(2L, 2L, getItemRequestDto()));
    }

    @Test
    void search_thenFoundItemRequestDtoListReturned() {
        when(repository.search(anyString())).thenReturn(List.of(getItem()));

        List<ItemRequestDto> actualItemRequestDtoList = service.search("item", 0, 10);

        assertEquals(1, actualItemRequestDtoList.size());
        assertEquals(getItemRequestDto(), actualItemRequestDtoList.get(0));
    }

    @Test
    void search_thenEmptyItemRequestDtoListReturned() {
        assertEquals(0, service.search("", 0, 10).size());
    }

    @Test
    void saveComment_thenSavedCommentDtoReturned() {
        User user = getUser();
        Item item = getItem();
        Comment comment = getComment();
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(any(), any(), any(), any())).thenReturn(List.of(new Booking()));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto actualCommentDto = service.saveComment(user.getId(), item.getId(), comment);

        assertEquals(getCommentDto(), actualCommentDto);
    }

    @Test
    void saveComment_thenBadRequestExceptionThrown() {
        User user = getUser();
        Item item = getItem();
        Comment comment = getComment();
        when(userService.getByIdWithCheck(anyLong())).thenReturn(user);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(any(), any(), any(), any())).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> service.saveComment(user.getId(), item.getId(), comment));
    }


    private User getUser() {
        return new User(1L, "userName", "email@email.ru");
    }

    private Item getItem() {
        return new Item(1L, "itemName", "description", true, 1L, null, null, null, null);
    }

    private ItemDto getItemDtoForOne() {
        return new ItemDto(1L, "itemName", "description", true, new ItemDto.BookingDto(1L, getUser().getId()), new ItemDto.BookingDto(1L, getUser().getId()), Set.of());
    }

    private ItemDto getItemDtoForAll() {
        return new ItemDto(1L, "itemName", "description", true, null, null, null);
    }

    private ItemRequestDto getItemRequestDto() {
        return new ItemRequestDto(1L, "itemName", "description", true, null);
    }

    private Comment getComment() {
        return new Comment(1L, "comment", getItem(), getUser(), null);
    }

    private CommentDto getCommentDto() {
        return new CommentDto(1L, "comment", getUser().getName(), null);
    }

    private Booking getBooking() {
        return new Booking(1L, null, null, null, getUser(), null);
    }
}
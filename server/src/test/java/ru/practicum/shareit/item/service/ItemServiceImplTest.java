package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ItemServiceImplTest {
    ItemService service;
    @Mock
    ItemRepository repository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserServiceImpl userService;
    @Mock
    CommentRepository commentRepository;
    User user;
    Item item;
    ItemRequestDto itemRequestDto;
    Comment comment;
    Booking booking;

    @BeforeEach
    public void setup() {
        service = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        user = new User(1L, "userName", "user@email.ru");
        item = new Item(1L, "itemName", "itemDescription", true, 1L, null, null, null, null);
        itemRequestDto = new ItemRequestDto(1L, "itemName", "itemDescription", true, null);
        comment = new Comment(1L, "comment", item, user, LocalDateTime.now());
        booking = new Booking(1L, null, null, null, user, null);
    }

    @Test
    void save() {
        when(userService.getByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.save(any()))
                .thenReturn(item);

        ItemRequestDto savedItemRequestDto = service.save(user.getId(), itemRequestDto);

        assertThat(savedItemRequestDto.getId()).isEqualTo(1L);
        assertThat(savedItemRequestDto.getName()).isEqualTo("itemName");
        assertThat(savedItemRequestDto.getDescription()).isEqualTo("itemDescription");
        assertThat(savedItemRequestDto.getAvailable()).isEqualTo(true);
    }

    @Test
    void update() {
        when(userService.getByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        itemRequestDto.setName("nameUpdated");
        ItemRequestDto updatedItem = service.update(1L, 1L, itemRequestDto);

        assertThat(updatedItem.getId()).isEqualTo(1L);
        assertThat(updatedItem.getName()).isEqualTo("nameUpdated");
        assertThat(updatedItem.getDescription()).isEqualTo("itemDescription");
        assertThat(updatedItem.getAvailable()).isEqualTo(true);

        assertThrows(ForbiddenException.class, () -> service.update(2L, 2L, itemRequestDto));
    }

    @Test
    void getById() {
        when(userService.getByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusEqualsOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(booking);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusEqualsOrderByStart(anyLong(), any(), any()))
                .thenReturn(booking);

        ItemDto foundItemDto = service.getById(user.getId(), itemRequestDto.getId());

        assertThat(foundItemDto.getId()).isEqualTo(1L);
        assertThat(foundItemDto.getName()).isEqualTo("itemName");
        assertThat(foundItemDto.getDescription()).isEqualTo("itemDescription");
        assertThat(foundItemDto.getAvailable()).isEqualTo(true);
    }

    @Test
    void getAllByOwnerId() {
        when(userService.getByIdWithCheck(anyLong()))
                .thenReturn(user);

        List<Item> items = List.of(item);

        when(repository.findAllByOwnerIdOrderById(anyLong()))
                .thenReturn(items);

        List<ItemDto> itemDtoList = service.getAllByOwnerId(1L, 0, 10);

        assertThat(itemDtoList.size()).isEqualTo(1);
        assertThat(itemDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(itemDtoList.get(0).getName()).isEqualTo("itemName");
        assertThat(itemDtoList.get(0).getDescription()).isEqualTo("itemDescription");
        assertThat(itemDtoList.get(0).getAvailable()).isEqualTo(true);
    }

    @Test
    void search() {
        Page<Item> items = new PageImpl<>(List.of(item));

        when(repository.search(anyString(), any()))
                .thenReturn(items);

        List<ItemRequestDto> itemRequestDtoList = service.search("item", 0, 20);

        assertThat(itemRequestDtoList.size()).isEqualTo(1);
        assertThat(itemRequestDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(itemRequestDtoList.get(0).getName()).isEqualTo("itemName");
        assertThat(itemRequestDtoList.get(0).getDescription()).isEqualTo("itemDescription");
        assertThat(itemRequestDtoList.get(0).getAvailable()).isEqualTo(true);

        assertThat(service.search("", 0, 20).size()).isEqualTo(0);
    }

    @Test
    void saveComment() {
        when(userService.getByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(any(), any(), any(), any()))
                .thenReturn(List.of(new Booking()));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentDto = service.saveComment(user.getId(), item.getId(), comment);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("comment");
        assertThat(commentDto.getAuthorName()).isEqualTo("userName");
        assertThat(commentDto.getCreated()).isNotNull();

        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeAndStatusEquals(any(), any(), any(), any()))
                .thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> service.saveComment(user.getId(), item.getId(), comment));
    }
}
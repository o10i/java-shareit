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
import ru.practicum.shareit.item.dto.ItemShortDto;
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
    ItemShortDto itemShortDto;
    Comment comment;

    @BeforeEach
    public void setup() {
        service = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        user = new User(1L, "userName", "user@email.ru");
        item = new Item(1L, "itemName", "itemDescription", true, 1L, null, null, null, null);
        itemShortDto = new ItemShortDto(1L, "itemName", "itemDescription", true, null);
        comment = new Comment(1L, "comment", item, user, LocalDateTime.now());
    }

    @Test
    void save() {
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.save(any()))
                .thenReturn(item);

        ItemShortDto savedItemShortDto = service.save(user.getId(), itemShortDto);

        assertThat(savedItemShortDto.getId()).isEqualTo(1L);
        assertThat(savedItemShortDto.getName()).isEqualTo("itemName");
        assertThat(savedItemShortDto.getDescription()).isEqualTo("itemDescription");
        assertThat(savedItemShortDto.getAvailable()).isEqualTo(true);
    }

    @Test
    void update() {
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        itemShortDto.setName("nameUpdated");
        ItemShortDto updatedItem = service.update(1L, 1L, itemShortDto);

        assertThat(updatedItem.getId()).isEqualTo(1L);
        assertThat(updatedItem.getName()).isEqualTo("nameUpdated");
        assertThat(updatedItem.getDescription()).isEqualTo("itemDescription");
        assertThat(updatedItem.getAvailable()).isEqualTo(true);

        assertThrows(ForbiddenException.class, () -> service.update(1L, 2L, itemShortDto));
    }

    @Test
    void findById() {
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemDto foundItemDto = service.findById(user.getId(), itemShortDto.getId());

        assertThat(foundItemDto.getId()).isEqualTo(1L);
        assertThat(foundItemDto.getName()).isEqualTo("itemName");
        assertThat(foundItemDto.getDescription()).isEqualTo("itemDescription");
        assertThat(foundItemDto.getAvailable()).isEqualTo(true);
    }

    @Test
    void findAllByOwnerId() {
        when(userService.findByIdWithCheck(anyLong()))
                .thenReturn(user);

        Page<Item> items = new PageImpl<>(List.of(item));

        when(repository.findAllByOwnerIdOrderById(anyLong(), any()))
                .thenReturn(items);

        List<ItemDto> itemDtoList = service.findAllByOwnerId(1L, 0, 20);

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

        List<ItemShortDto> itemShortDtoList = service.search("item", 0, 20);

        assertThat(itemShortDtoList.size()).isEqualTo(1);
        assertThat(itemShortDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(itemShortDtoList.get(0).getName()).isEqualTo("itemName");
        assertThat(itemShortDtoList.get(0).getDescription()).isEqualTo("itemDescription");
        assertThat(itemShortDtoList.get(0).getAvailable()).isEqualTo(true);

        assertThat(service.search("", 0, 20).size()).isEqualTo(0);
    }

    @Test
    void saveComment() {
        when(userService.findByIdWithCheck(anyLong()))
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
package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @InjectMocks
    private RequestServiceImpl service;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private RequestRepository repository;
    @Mock
    private ItemRepository itemRepository;
    private User requestor;
    private Request request;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestor = new User(1L, "name", "email@email.ru");
        request = new Request(1L, "description", requestor, null, null);
        requestDto = new RequestDto(1L, "description", null, List.of());
    }

    @Test
    void save_thenSavedRequestDtoReturned() {
        when(userService.getByIdWithCheck(anyLong())).thenReturn(requestor);
        when(repository.save(any())).thenReturn(request);

        RequestDto actualRequestDto = service.save(request.getId(), new RequestRequestDto("description"));

        assertEquals(requestDto, actualRequestDto);
    }

    @Test
    void getById_thenFoundRequestDtoReturned() {
        when(userService.getByIdWithCheck(any())).thenReturn(requestor);
        when(repository.findById(any())).thenReturn(Optional.of(request));

        RequestDto actualRequestDto = service.getById(requestor.getId(), request.getId());

        assertEquals(requestDto, actualRequestDto);
    }

    @Test
    void getAll_thenFoundRequestDtoListReturned() {
        when(repository.findAll()).thenReturn(List.of(request));

        List<RequestDto> requestDtos = service.getAll(2L, 0, 10);

        assertEquals(1, requestDtos.size());
        assertEquals(requestDto, requestDtos.get(0));
    }

    @Test
    void getAllByRequestorId_thenFoundRequestDtoListReturned() {
        when(repository.findAllByRequestorOrderByCreatedDesc(any())).thenReturn(List.of(request));

        List<RequestDto> requestDtos = service.getAllByRequestorId(requestor.getId());

        assertEquals(1, requestDtos.size());
        assertEquals(requestDto, requestDtos.get(0));
    }
}
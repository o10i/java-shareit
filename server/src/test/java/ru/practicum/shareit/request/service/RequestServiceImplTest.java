package ru.practicum.shareit.request.service;

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

    @Test
    void save_thenSavedRequestDtoReturned() {
        Request request = getRequest();
        when(userService.getByIdWithCheck(anyLong())).thenReturn(getUser());
        when(repository.save(any())).thenReturn(request);

        RequestDto actualRequestDto = service.save(request.getId(), new RequestRequestDto("description"));

        assertEquals(getRequestDto(), actualRequestDto);
    }

    @Test
    void getById_thenFoundRequestDtoReturned() {
        User requestor = getUser();
        Request request = getRequest();
        when(userService.getByIdWithCheck(any())).thenReturn(requestor);
        when(repository.findById(any())).thenReturn(Optional.of(request));

        RequestDto actualRequestDto = service.getById(requestor.getId(), request.getId());

        assertEquals(getRequestDto(), actualRequestDto);
    }

    @Test
    void getAll_thenFoundRequestDtoListReturned() {
        when(repository.findAll()).thenReturn(List.of(getRequest()));

        User requestor = getUser();
        requestor.setId(2L);
        RequestDto requestDto = getRequestDto();
        List<RequestDto> requestDtos = service.getAll(requestor.getId(), 0, 10);

        assertEquals(1, requestDtos.size());
        assertEquals(requestDto, requestDtos.get(0));
    }

    @Test
    void getAllByRequestorId_thenFoundRequestDtoListReturned() {
        when(repository.findAllByRequestorOrderByCreatedDesc(any())).thenReturn(List.of(getRequest()));

        List<RequestDto> requestDtos = service.getAllByRequestorId(getUser().getId());

        assertEquals(1, requestDtos.size());
        assertEquals(getRequestDto(), requestDtos.get(0));
    }

    private User getUser() {
        return new User(1L, "name", "email@email.ru");
    }

    private Request getRequest() {
        return new Request(1L, "description", getUser(), null, null);
    }

    private RequestDto getRequestDto() {
        return new RequestDto(1L, "description", null, List.of());
    }
}
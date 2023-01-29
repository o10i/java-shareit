package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class RequestServiceImplTest {
    @Mock
    RequestRepository repository;
    RequestService service;
    @Mock
    UserServiceImpl userService;
    @Mock
    ItemRepository itemRepository;
    User requestor;
    Request request;
    RequestRequestDto requestRequestDto;

    @BeforeEach
    public void setup() {
        service = new RequestServiceImpl(repository, userService, itemRepository);
        requestor = new User(1L, "name", "email@email.ru");
        request = new Request(1L, "description", requestor, Instant.now(), null);
        requestRequestDto = new RequestRequestDto("description");
    }

    @Test
    void save() {
        when(userService.getByIdWithCheck(any()))
                .thenReturn(requestor);
        when(repository.save(any()))
                .thenReturn(request);

        RequestDto savedRequestDto = service.save(request.getId(), requestRequestDto);

        assertThat(savedRequestDto.getId()).isEqualTo(1L);
        assertThat(savedRequestDto.getDescription()).isEqualTo("description");
        assertThat(savedRequestDto.getCreated()).isNotNull();
        assertThat(savedRequestDto.getItems()).isEqualTo(List.of());
    }

    @Test
    void findAllByRequestor() {
        when(repository.findAllByRequestorOrderByCreatedDesc(any()))
                .thenReturn(List.of(request));

        List<RequestDto> requestDtos = service.getAllByRequestorId(requestor.getId());

        assertThat(requestDtos.size()).isEqualTo(1);
        assertThat(requestDtos.get(0).getId()).isEqualTo(1L);
        assertThat(requestDtos.get(0).getDescription()).isEqualTo("description");
        assertThat(requestDtos.get(0).getCreated()).isNotNull();
        assertThat(requestDtos.get(0).getItems()).isEqualTo(List.of());
    }

    @Test
    void findAll() {
        request.setRequestor(new User(2L, "name2", "email2@email.ru"));

        Page<Request> requests = new PageImpl<>(List.of(request));

        when(repository.findAll(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "created"))))
                .thenReturn(requests);

        List<RequestDto> requestDtos = service.getAll(requestor.getId(), 0, 20);

        assertThat(requestDtos.size()).isEqualTo(1);
        assertThat(requestDtos.get(0).getId()).isEqualTo(1L);
        assertThat(requestDtos.get(0).getDescription()).isEqualTo("description");
        assertThat(requestDtos.get(0).getCreated()).isNotNull();
        assertThat(requestDtos.get(0).getItems()).isEqualTo(List.of());
    }

    @Test
    void findById() {
        when(userService.getByIdWithCheck(any()))
                .thenReturn(requestor);
        when(repository.findById(any()))
                .thenReturn(Optional.of(request));

        RequestDto savedRequestDto = service.getById(requestor.getId(), request.getId());

        assertThat(savedRequestDto.getId()).isEqualTo(1L);
        assertThat(savedRequestDto.getDescription()).isEqualTo("description");
        assertThat(savedRequestDto.getCreated()).isNotNull();
        assertThat(savedRequestDto.getItems()).isEqualTo(List.of());
    }
}
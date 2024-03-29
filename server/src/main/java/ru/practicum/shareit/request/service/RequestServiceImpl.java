package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.RequestMapper.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    RequestRepository repository;
    UserServiceImpl userService;
    ItemRepository itemRepository;

    @Override
    public RequestDto save(Long requestorId, RequestRequestDto requestRequestDto) {
        User requestor = userService.getByIdWithCheck(requestorId);
        return toRequestDto(repository.save(toRequest(requestRequestDto, requestor)));
    }

    @Override
    public RequestDto getById(Long userId, Long requestId) {
        userService.getByIdWithCheck(userId);

        Request request = getByIdWithCheck(requestId);
        request.setItems(itemRepository.findAllByRequestId(requestId));

        return toRequestDto(request);
    }

    @Override
    public List<RequestDto> getAll(Long userId, Integer from, Integer size) {
        List<Request> requests = repository.findAll().stream()
                .filter(request -> !request.getRequestor().getId().equals(userId))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        setItems(requests);

        return toRequestDtoList(requests);
    }

    @Override
    public List<RequestDto> getAllByRequestorId(Long requestorId) {
        User requestor = userService.getByIdWithCheck(requestorId);

        List<Request> requests = repository.findAllByRequestorOrderByCreatedDesc(requestor);

        setItems(requests);

        return toRequestDtoList(requests);
    }

    private Request getByIdWithCheck(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d not found", requestId)));
    }

    private void setItems(List<Request> requests) {
        List<Long> requestsId = requests.stream().map(request -> request.getRequestor().getId()).collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestsId);
        requests.forEach(request -> request
                .setItems(items.stream()
                        .filter(item -> item.getRequestId().equals(request.getRequestor().getId()))
                        .collect(Collectors.toList())));
    }
}

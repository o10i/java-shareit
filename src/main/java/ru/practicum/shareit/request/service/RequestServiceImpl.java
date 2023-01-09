package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    RequestRepository repository;
    UserServiceImpl userService;
    ItemServiceImpl itemService;

    @Override
    public RequestDto save(Long requestorId, RequestDto requestDto) {
        userService.findByIdWithException(requestorId);
        Request request = RequestMapper.toRequest(requestDto, requestorId);
        return RequestMapper.toRequestDto(repository.save(request));
    }

    @Override
    public List<RequestWithItemsDto> findAllByRequestorOrderByCreatedDesc(Long userId) {
        userService.findByIdWithException(userId);

        return toRequestsDto(repository.findAllByRequestorOrderByCreatedDesc(userId));
    }

    @Override
    public List<RequestWithItemsDto> findAll(Long userId, Integer from, Integer size) {
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from / size, size, sortByCreated);

        List<Request> requests = repository.findAll(page).stream()
                .filter(request -> !request.getRequestor().equals(userId))
                .collect(Collectors.toList());

        return toRequestsDto(requests);
    }

    @Override
    public RequestWithItemsDto findById(Long userId, Long requestId) {
        userService.findByIdWithException(userId);

        Request request = findByIdWithException(requestId);
        List<ItemDto> itemDtos = itemService.findAllByRequestId(requestId);

        return RequestMapper.toRequestWithItemsDto(request, itemDtos);
    }

    private List<RequestWithItemsDto> toRequestsDto(List<Request> requests) {
        return requests.stream()
                .map(request -> RequestMapper.toRequestWithItemsDto(request, itemService.findAllByRequestId(request.getId())))
                .collect(Collectors.toList());
    }

    private Request findByIdWithException(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d not found", requestId)));
    }
}

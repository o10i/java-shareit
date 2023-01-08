package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.Instant;
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
    public RequestDto save(Long userId, RequestDto requestDto) {
        userService.findByIdWithException(userId);

        requestDto.setRequestor(userId);
        requestDto.setCreated(Instant.now());
        Request request = RequestMapper.toRequest(requestDto);
        return RequestMapper.toRequestDto(repository.save(request));
    }

    @Override
    public List<RequestDto> findAllByRequestorOrderByCreatedDesc(Long userId) {
        userService.findByIdWithException(userId);

        List<RequestDto> requestsDto = toRequestsDto(repository.findAllByRequestorOrderByCreatedDesc(userId));
        requestsDto.forEach(requestDto -> requestDto.setItems(itemService.findAllByRequestId(requestDto.getId())));

        return requestsDto;
    }

    @Override
    public List<RequestDto> findAll(Long userId, Integer from, Integer size) {
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from / size, size, sortByCreated);

        Page<Request> requestPage = repository.findAll(page);

        List<RequestDto> requestsDto = requestPage.stream()
                .filter(request -> !request.getRequestor().equals(userId))
                .map(RequestMapper::toRequestDto).collect(Collectors.toList());

        requestsDto.forEach(requestDto -> requestDto.setItems(itemService.findAllByRequestId(requestDto.getId())));
        return requestsDto;
    }

    @Override
    public RequestDto findById(Long userId, Long requestId) {
        userService.findByIdWithException(userId);

        RequestDto requestDto = RequestMapper.toRequestDto(findByIdWithException(requestId));
        requestDto.setItems(itemService.findAllByRequestId(requestDto.getId()));
        return requestDto;
    }

    private List<RequestDto> toRequestsDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    private Request findByIdWithException(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d not found", requestId)));
    }
}

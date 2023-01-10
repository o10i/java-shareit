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
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
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
    public Request save(Request request) {
        userService.findByIdWithCheck(request.getRequestor());
        return repository.save(request);
    }

    @Override
    public List<Request> findAllByRequestor(Long requestorId) {
        userService.findByIdWithCheck(requestorId);

        List<Request> requests = repository.findAllByRequestorOrderByCreatedDesc(requestorId);
        requests.forEach(request -> request.setItems(itemService.findAllByRequestId(request.getId())));

        return requests;
    }

    @Override
    public List<Request> findAll(Long userId, Integer from, Integer size) {
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");

        Pageable page = PageRequest.of(from / size, size, sortByCreated);

        List<Request> requests = repository.findAll(page).stream()
                .filter(request -> !request.getRequestor().equals(userId))
                .collect(Collectors.toList());
        requests.forEach(request -> request.setItems(itemService.findAllByRequestId(request.getId())));

        return requests;
    }

    @Override
    public Request findById(Long userId, Long requestId) {
        userService.findByIdWithCheck(userId);

        Request request = findByIdWithException(requestId);
        request.setItems(itemService.findAllByRequestId(requestId));

        return request;
    }

    private Request findByIdWithException(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d not found", requestId)));
    }
}

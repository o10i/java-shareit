package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.Request;

import java.util.List;

public interface RequestService {
    Request save(Request request);

    List<Request> findAllByRequestor(Long userId);

    List<Request> findAll(Long userId, Integer from, Integer size);

    Request findById(Long userId, Long requestId);
}

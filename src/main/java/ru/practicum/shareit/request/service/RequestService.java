package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestShortDto;

import java.util.List;

public interface RequestService {
    RequestDto save(Long requestorId, RequestShortDto requestShortDto);

    List<RequestDto> findAllByRequestor(Long userId);

    List<RequestDto> findAll(Long userId, Integer from, Integer size);

    RequestDto findById(Long userId, Long requestId);
}

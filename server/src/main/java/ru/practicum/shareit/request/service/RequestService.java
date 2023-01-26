package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestShortDto;

import java.util.List;

public interface RequestService {
    RequestDto add(Long requestorId, RequestShortDto requestShortDto);

    List<RequestDto> getAllByRequestor(Long userId);

    List<RequestDto> getAll(Long userId, Integer from, Integer size);

    RequestDto getById(Long userId, Long requestId);
}

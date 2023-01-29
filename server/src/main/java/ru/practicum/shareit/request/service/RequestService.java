package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import java.util.List;

public interface RequestService {
    RequestDto save(Long requestorId, RequestRequestDto requestRequestDto);

    List<RequestDto> getAllByRequestorId(Long userId);

    List<RequestDto> getAll(Long userId, Integer from, Integer size);

    RequestDto getById(Long userId, Long requestId);
}

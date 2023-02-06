package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import java.util.List;

public interface RequestService {
    RequestDto save(Long requestorId, RequestRequestDto requestRequestDto);

    RequestDto getById(Long userId, Long requestId);

    List<RequestDto> getAll(Long userId, Integer from, Integer size);

    List<RequestDto> getAllByRequestorId(Long requestorId);
}

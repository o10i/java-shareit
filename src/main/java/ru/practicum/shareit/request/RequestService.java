package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto save(Long userId, RequestDto requestDto);

    List<RequestWithItemsDto> findAllByRequestorOrderByCreatedDesc(Long userId);

    List<RequestWithItemsDto> findAll(Long userId, Integer from, Integer size);

    RequestWithItemsDto findById(Long userId, Long requestId);
}

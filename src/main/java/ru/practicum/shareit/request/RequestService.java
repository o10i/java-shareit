package ru.practicum.shareit.request;

import java.util.List;

public interface RequestService {
    RequestDto save(Long userId, RequestDto requestDto);

    List<RequestDto> findAllByRequestorOrderByCreatedDesc(Long userId);

    List<RequestDto> findAll(Long userId, Integer from, Integer size);

    RequestDto findById(Long userId, Long requestId);
}

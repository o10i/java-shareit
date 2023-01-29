package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getDescription(),
                request.getCreated(),
                request.getItems() == null ? List.of() : ItemMapper.toListItemShortDto(request.getItems()));
    }

    public static List<RequestDto> toListRequestDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    public static Request toRequest(RequestRequestDto requestRequestDto, User requestor) {
        Request request = new Request();
        request.setDescription(requestRequestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(Instant.now());
        return request;
    }
}

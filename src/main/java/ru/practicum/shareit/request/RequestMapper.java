package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestShortDto;
import ru.practicum.shareit.user.User;

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

    public static Request toRequest(RequestShortDto requestShortDto, User requestor) {
        Request request = new Request();
        request.setDescription(requestShortDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(Instant.now());
        return request;
    }
}

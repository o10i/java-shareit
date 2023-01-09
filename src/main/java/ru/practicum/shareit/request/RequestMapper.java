package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getDescription(),
                request.getCreated());
    }

    public static RequestWithItemsDto toRequestWithItemsDto(Request request, List<ItemDto> itemDtos) {
        return new RequestWithItemsDto(request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtos
        );
    }

    public static Request toRequest(RequestDto requestDto, Long requestorId) {
        Request request = new Request();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestorId);
        request.setCreated(Instant.now());
        return request;
    }
}

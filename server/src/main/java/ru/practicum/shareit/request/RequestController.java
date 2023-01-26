package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestShortDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService service;

    @PostMapping()
    public RequestDto add(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                          @RequestBody RequestShortDto requestShortDto) {
        return service.add(requestorId, requestShortDto);
    }

    @GetMapping("/{requestId}")
    public RequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long requestId) {
        return service.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(defaultValue = "0") Integer from,
                                   @RequestParam(defaultValue = "20") Integer size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping()
    public List<RequestDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return service.getAllByRequestor(requestorId);
    }
}

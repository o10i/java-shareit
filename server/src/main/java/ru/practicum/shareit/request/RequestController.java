package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService service;

    @PostMapping()
    public RequestDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody RequestRequestDto requestRequestDto) {
        return service.save(userId, requestRequestDto);
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
    public List<RequestDto> getAllByRequestorId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllByRequestorId(userId);
    }
}

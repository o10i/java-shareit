package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestShortDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService service;

    @PostMapping()
    public RequestDto save(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                           @Valid @RequestBody RequestShortDto requestShortDto) {
        return service.save(requestorId, requestShortDto);
    }

    @GetMapping()
    public List<RequestDto> findAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return service.findAllByRequestor(requestorId);
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return service.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long requestId) {
        return service.findById(userId, requestId);
    }
}

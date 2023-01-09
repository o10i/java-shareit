package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestServiceImpl service;

    @PostMapping()
    public RequestDto save(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                           @Valid @RequestBody RequestDto requestDto) {
        return service.save(requestorId, requestDto);
    }

    @GetMapping()
    public List<RequestWithItemsDto> findAllByRequestorOrderByCreatedDesc(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findAllByRequestorOrderByCreatedDesc(userId);
    }

    @GetMapping("/all")
    public List<RequestWithItemsDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return service.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestWithItemsDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long requestId) {
        return service.findById(userId, requestId);
    }
}

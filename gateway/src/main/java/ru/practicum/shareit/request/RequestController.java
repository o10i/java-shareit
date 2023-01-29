package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody @Valid RequestRequestDto requestDto) {
        log.info("Create request {}, userId={}", requestDto, userId);
        return requestClient.save(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        log.info("Get requestId={}, userId={}", requestId, userId);
        return requestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all requests by userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestorId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests by requestor with userId={}", userId);
        return requestClient.getAllByRequestorId(userId);
    }
}
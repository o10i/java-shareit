package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid UserRequestDto requestDto) {
        log.info("Creatе user {}", requestDto);
        return userClient.add(requestDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getByid(@PathVariable long userId) {
        log.info("Get userId={}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get all users");
        return userClient.getAll();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                         @RequestBody UserRequestDto requestDto) {
        log.info("Updatе user {} userId={}", requestDto, userId);
        return userClient.update(userId, requestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Delete user userId={}", userId);
        return userClient.delete(userId);
    }
}
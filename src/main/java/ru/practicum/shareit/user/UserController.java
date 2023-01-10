package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.user.UserMapper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl service;

    @PostMapping()
    public UserDto save(@Valid @RequestBody UserDto userDto) {
        return toUserDto(service.save(toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        return toUserDto(service.update(userId, toUser(userDto)));
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return toUserDto(service.findByIdWithCheck(userId));
    }

    @GetMapping()
    public List<UserDto> findAll() {
        return toListUserDto(service.findAll());
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        service.deleteById(userId);
    }
}

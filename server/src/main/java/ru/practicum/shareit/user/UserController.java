package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping()
    public UserDto save(@RequestBody UserDto userDto) {
        return service.save(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getByid(@PathVariable Long userId) {
        return service.getByid(userId);
    }

    @GetMapping()
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        return service.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        service.delete(userId);
    }
}

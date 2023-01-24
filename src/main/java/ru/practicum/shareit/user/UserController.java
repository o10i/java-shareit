package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping()
    public UserDto save(@Valid @RequestBody UserDto userDto) {
        return service.save(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        return service.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return service.findById(userId);
    }

    @GetMapping()
    public List<UserDto> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        service.deleteById(userId);
    }
}

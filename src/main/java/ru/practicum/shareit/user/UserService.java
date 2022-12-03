package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public UserDto create(UserDto userDto) {
        User user = repository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto update(Long userId, UserDto userDto) {
        User user = repository.update(userId, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto getById(Long userId) {
        User user = repository.getById(userId);
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getAll() {
        List<User> users = repository.getAll();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void deleteById(Long userId) {
        repository.deleteById(userId);
    }
}

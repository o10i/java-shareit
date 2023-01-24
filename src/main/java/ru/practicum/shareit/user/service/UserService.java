package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto findById(Long userId);

    List<UserDto> findAll();

    void deleteById(Long userId);
}

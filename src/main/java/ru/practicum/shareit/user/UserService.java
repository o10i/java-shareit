package ru.practicum.shareit.user;

import java.util.List;

interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto findById(Long userId);

    List<UserDto> findAll();

    void deleteById(Long userId);
}

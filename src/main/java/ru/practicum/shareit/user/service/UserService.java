package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

interface UserService {
    User save(User user);

    User update(Long userId, User user);

    User findByIdWithCheck(Long userId);

    List<User> findAll();

    void deleteById(Long userId);
}

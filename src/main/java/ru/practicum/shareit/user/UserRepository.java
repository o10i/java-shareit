package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User update(Long userId, User user);

    User getById(Long id);

    List<User> getAll();

    void deleteById(Long userId);
}

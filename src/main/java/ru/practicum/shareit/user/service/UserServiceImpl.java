package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Transactional
    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Transactional
    @Override
    public User update(Long userId, User user) {
        User userToUpdate = findById(userId);
        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userToUpdate.setEmail(user.getEmail());
        }
        return userToUpdate;
    }

    @Override
    public User findById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", userId)));
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public void deleteById(Long userId) {
        repository.deleteById(userId);
    }
}

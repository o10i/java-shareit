package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static ru.practicum.shareit.user.UserMapper.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Transactional
    @Override
    public UserDto save(UserDto userDto) {
        return toUserDto(repository.save(toUser(userDto)));
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userToUpdate = findByIdWithCheck(userId);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            userToUpdate.setEmail(userDto.getEmail());
        }
        return toUserDto(userToUpdate);
    }

    @Override
    public UserDto getByid(Long userId) {
        return toUserDto(findByIdWithCheck(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return toListUserDto(repository.findAll());
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        repository.deleteById(userId);
    }

    public User findByIdWithCheck(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", userId)));
    }
}

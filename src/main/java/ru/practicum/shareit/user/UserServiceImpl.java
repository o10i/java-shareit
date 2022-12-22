package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto save(UserDto userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with id=%d not found", userId)));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with id=%d not found", userId)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return UserMapper.toUsersDto(repository.findAll());
    }

    @Override
    public void deleteById(Long userId) {
        repository.deleteById(userId);
    }
}

package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryImpl implements UserRepository {
    final Map<Long, User> users = new HashMap<>();
    Long idCounter = 1L;

    @Override
    public User create(User user) {
        checkEmailUniqueness(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User updatedUser = getById(userId);
        if (user.getEmail() != null) {
            checkEmailUniqueness(user);
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        return updatedUser;
    }

    @Override
    public User getById(Long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with id=%d not found", userId)));
    }

    @Override
    public List<User> getAll() {
        return  new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
    }

    private void checkEmailUniqueness(User user) {
        if (users.values().stream().map(User::getEmail).anyMatch(s -> s.equals(user.getEmail()))) {
            throw new IllegalArgumentException(String.format("Email %s already exists", user.getEmail()));
        }
    }
}

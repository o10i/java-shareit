package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryImpl implements UserRepository {
    final List<User> users = new ArrayList<>();
    Long idCounter = 1L;

    @Override
    public User create(User user) {
        checkEmailUniqueness(user);
        user.setId(idCounter++);
        users.add(user);
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
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with id=%d not found", userId)));
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(getById(userId));
    }

    private void checkEmailUniqueness(User user) {
        if (users.stream().map(User::getEmail).anyMatch(s -> s.equals(user.getEmail()))) {
            throw new IllegalArgumentException(String.format("Email %s already exists", user.getEmail()));
        }
    }
}

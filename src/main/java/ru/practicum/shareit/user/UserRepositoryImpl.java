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
        System.out.println(2);
        User updatedUser = users.stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with %d id not found", userId)));
        System.out.println(3);
        if (user.getEmail() != null) {
            checkEmailUniqueness(user);
            updatedUser.setEmail(user.getEmail());
        }
        System.out.println(4);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        System.out.println(5);
        return updatedUser;
    }

    @Override
    public User getById(Long userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with %d id not found", userId)));
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

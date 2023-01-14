package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {
    @Mock
    UserRepository repository;
    UserService service;
    User user;
    UserDto userDto;

    @BeforeEach
    public void setup() {
        service = new UserServiceImpl(repository);
        user = new User(1L, "name", "email@email.ru");
        userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    @Test
    void save() {
        when(repository.save(any()))
                .thenReturn(user);

        UserDto savedUserDto = service.save(userDto);

        assertThat(savedUserDto.getId()).isEqualTo(1L);
        assertThat(savedUserDto.getName()).isEqualTo("name");
        assertThat(savedUserDto.getEmail()).isEqualTo("email@email.ru");
    }

    @Test
    void update() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userDto.setName("nameUpdated");

        UserDto updatedUserDto = service.update(user.getId(), userDto);

        assertThat(updatedUserDto.getId()).isEqualTo(1L);
        assertThat(updatedUserDto.getName()).isEqualTo("nameUpdated");
        assertThat(updatedUserDto.getEmail()).isEqualTo("email@email.ru");
    }

    @Test
    void findById() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto updatedUserDto = service.findById(user.getId());

        assertThat(updatedUserDto.getId()).isEqualTo(1L);
        assertThat(updatedUserDto.getName()).isEqualTo("name");
        assertThat(updatedUserDto.getEmail()).isEqualTo("email@email.ru");
    }

    @Test
    void findAll() {
        when(repository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> userDtos = service.findAll();

        assertThat(userDtos.size()).isEqualTo(1);
        assertThat(userDtos.get(0).getId()).isEqualTo(1L);
        assertThat(userDtos.get(0).getName()).isEqualTo("name");
        assertThat(userDtos.get(0).getEmail()).isEqualTo("email@email.ru");
    }

    @Test
    void deleteById() {
        service.deleteById(1L);

        verify(repository, times(1))
                .deleteById(1L);
    }
}
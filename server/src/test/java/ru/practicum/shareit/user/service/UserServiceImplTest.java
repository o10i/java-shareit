package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl service;
    @Mock
    private UserRepository repository;

    @Test
    void save_thenSavedUser() {
        User user = new User();
        UserDto expectedUserDto = new UserDto();
        when(repository.save(any())).thenReturn(user);

        UserDto actualUserDto = service.save(expectedUserDto);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void update() {
/*        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        userDto.setName("nameUpdated");

        UserDto actualUserDto = service.update(user.getId(), userDto);

        assertEquals(actualUserDto, userDto);*/
    }

    @Test
    void findById_whenUserFound_thenReturnedUser() {
        long userId = 0L;
        User user = new User();
        UserDto expectedUserDto = new UserDto();
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUserDto = service.getByid(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void findById_whenUserFound_thenNotFoundExceptionThrown() {
        long userId = 0L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getByid(userId));
    }

    @Test
    void findAll() {
        User expectedUser = new User();
        UserDto expectedUserDto = new UserDto();
        when(repository.findAll()).thenReturn(List.of(expectedUser));

        List<UserDto> actualUserDtoList = service.getAll();

        assertEquals(1, actualUserDtoList.size());
        assertEquals(expectedUserDto, actualUserDtoList.get(0));
    }

    @Test
    void deleteById() {
        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
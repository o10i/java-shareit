package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    void save_thenSavedUserDtoReturned() {
        when(repository.save(any())).thenReturn(getUser());

        UserDto userDto = getUserDto();
        UserDto actualUserDto = service.save(userDto);

        assertEquals(userDto, actualUserDto);
    }

    @Test
    void getById_thenFoundUserDtoReturned() {
        User user = getUser();
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto actualUserDto = service.getByid(user.getId());

        assertEquals(getUserDto(), actualUserDto);
    }

    @Test
    void getById_thenNotFoundExceptionThrown() {
        when(repository.findById(0L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getByid(0L));
    }

    @Test
    void getAll_thenFoundUserDtoListReturned() {
        when(repository.findAll()).thenReturn(List.of(getUser()));

        List<UserDto> actualUserDtoList = service.getAll();

        assertEquals(1, actualUserDtoList.size());
        assertEquals(getUserDto(), actualUserDtoList.get(0));
    }

    @Test
    void update_thenUpdatedUserDtoReturned() {
        User user = getUser();
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto userDto = getUserDto();
        userDto.setName("name2");
        userDto.setEmail("email2@email.ru");
        UserDto actualUserDto = service.update(user.getId(), userDto);

        assertEquals(userDto, actualUserDto);
    }

    @Test
    void deleteById_thenUserDeleted() {
        service.delete(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    private User getUser() {
        return new User(1L, "name", "email@email.ru");
    }

    private UserDto getUserDto() {
        return new UserDto(1L, "name", "email@email.ru");
    }
}
package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final UserMapper userMapper;

    @InjectMocks
    private final UserService userService;

    private final User user = User.builder()
            .name("Ivan")
            .email("ivan@test.com")
            .build();

    private final UserDto userDto = UserDto.builder()
            .name("Ivan")
            .email("ivan@test.com")
            .build();

    @Test
    public void createUserSavesUser() {
        Mockito.when(userMapper.mapToModel(userDto)).thenReturn(user);

        userService.createUser(userDto);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void updateUserUpdatesUser() {
        User existing = user;
        existing.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.updateUser(1L, userDto);

        verify(userRepository, times(1)).save(existing);
    }

    @Test
    public void updateUserThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.updateUser(1L, userDto));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void getUserByIdThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.getUserById(1L));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void deleteUserByIdDeletesUser() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUserByIdThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(1L));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void getAllUsersReturnsUsers() {
        userService.getAllUsers();

        verify(userRepository, times(1)).findAll();
    }
}

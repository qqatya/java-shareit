package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.UserCreate;
import ru.practicum.shareit.user.validation.UserUpdate;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Создание пользователя
     *
     * @param dto Объект, содержащий данные для создания
     * @return Созданный пользователь
     */
    @PostMapping
    public UserDto createUser(@Validated(UserCreate.class) @RequestBody UserDto dto) {
        return userService.createUser(dto);
    }

    /**
     * Обновление пользователя
     *
     * @param id  Идентификатор пользователя
     * @param dto Объект, содержащий данные для обновления
     * @return Обновленный пользователь
     */
    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @Validated(UserUpdate.class) @RequestBody UserDto dto) {
        return userService.updateUser(id, dto);
    }

    /**
     * Получение пользователя по идентификатору
     *
     * @param id Идентификатор пользователя
     * @return Пользователь
     */
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * Удаление пользователя по идентификатору
     *
     * @param id Идентификатор пользователя
     */
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    /**
     * Получение всех пользователей
     *
     * @return Список пользователей
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

}

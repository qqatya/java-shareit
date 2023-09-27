package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.UserCreate;
import ru.practicum.shareit.user.validation.UserUpdate;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    /**
     * Создание пользователя
     *
     * @param dto Объект, содержащий данные для создания
     * @return Созданный пользователь
     */
    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(UserCreate.class) @RequestBody UserDto dto) {
        return userClient.createUser(dto);
    }

    /**
     * Обновление пользователя
     *
     * @param id  Идентификатор пользователя
     * @param dto Объект, содержащий данные для обновления
     * @return Обновленный пользователь
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                              @Validated(UserUpdate.class) @RequestBody UserDto dto) {
        return userClient.updateUser(id, dto);
    }

    /**
     * Получение пользователя по идентификатору
     *
     * @param id Идентификатор пользователя
     * @return Пользователь
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return userClient.getUserById(id);
    }

    /**
     * Удаление пользователя по идентификатору
     *
     * @param id Идентификатор пользователя
     */
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userClient.deleteUserById(id);
    }

    /**
     * Получение всех пользователей
     *
     * @return Список пользователей
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }
}

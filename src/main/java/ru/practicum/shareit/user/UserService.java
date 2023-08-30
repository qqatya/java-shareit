package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    /**
     * Создание пользователя
     *
     * @param dto Объект, содержащий данные для создания
     * @return Созданный пользователь
     */
    UserDto createUser(UserDto dto);

    /**
     * Обновление пользователя
     *
     * @param id  Идентификатор пользователя
     * @param dto Объект, содержащий данные для обновления
     * @return Обновленный пользователь
     */
    UserDto updateUser(Long id, UserDto dto);

    /**
     * Получение пользователя по идентификатору
     *
     * @param id Идентификатор пользователя
     * @return Пользователь
     */
    UserDto getUserById(Long id);

    /**
     * Удаление пользователя по идентификатору
     *
     * @param id Идентификатор пользователя
     */
    void deleteUserById(Long id);

    /**
     * Получение всех пользователей
     *
     * @return Список пользователей
     */
    List<UserDto> getAllUsers();

}

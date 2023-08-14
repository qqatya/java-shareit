package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
     * Создание пользователя
     *
     * @param user Объект, содержащий данные для создания
     * @return Созданный пользователь
     */
    User save(User user);

    /**
     * Обновление пользователя
     *
     * @param user Объект, содержащий данные для обновления
     * @return Обновленный пользователь
     */
    User update(User user);

    /**
     * Получение пользователя по идентификатору
     *
     * @param id Идентификатор пользователя
     * @return Пользователь (пустой, если такого пользователя нет)
     */
    Optional<User> getById(Long id);

    /**
     * Удаление пользователя по идентификатору
     *
     * @param id Идентификатор пользователя
     */
    void deleteById(Long id);

    /**
     * Получение всех пользователей
     *
     * @return Список пользователей
     */
    List<User> getAll();

    /**
     * Проверка уникальности эл. почты
     *
     * @param email Адрес эл. почты
     * @return Список идентификаторов пользователей с эл. почтой, как в аргументе метода
     */
    List<Long> getEmailDuplicates(String email);

    /**
     * Проверка на существование пользователя
     *
     * @param id Идентификатор пользователя
     * @return Признак существования
     */
    boolean doesExist(Long id);

}

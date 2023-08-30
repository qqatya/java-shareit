package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Проверка уникальности эл. почты
     *
     * @param email Адрес эл. почты
     * @return Список идентификаторов пользователей с эл. почтой, как в аргументе метода
     */
    @Query("SELECT u.id FROM User u WHERE u.email = ?1")
    List<Long> getEmailDuplicates(String email);

}

package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.type.ExceptionType.USER_NOT_FOUND;

@Repository
public class UserRepositoryImpl implements UserRepository {

    /**
     * Ключ - id вещи, значение - хранимый объект
     **/
    private final Map<Long, User> users = new HashMap<>();

    private Long userIdCounter = 0L;

    @Override
    public User save(User user) {
        user.setId(++userIdCounter);
        users.put(user.getId(), user);
        Long userId = user.getId();

        return getById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getValue() + userId));
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        Long userId = user.getId();

        return getById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getValue() + userId));
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<Long> getEmailDuplicates(String email) {
        return users.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean doesExist(Long id) {
        return users.get(id) != null;
    }
}

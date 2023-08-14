package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.type.ExceptionType.USER_NOT_FOUND;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();

    private Long userIdCounter = 0L;

    @Override
    public User save(User user) {
        user.setId(++userIdCounter);
        users.add(user);
        Long userId = user.getId();

        return getById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getValue() + userId));
    }

    @Override
    public User update(User user) {
        deleteById(user.getId());
        users.add(user);
        Long userId = user.getId();

        return getById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getValue() + userId));
    }

    @Override
    public Optional<User> getById(Long id) {
        return users.stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        Optional<User> toBeRemoved = getById(id);

        toBeRemoved.ifPresent(users::remove);
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public List<Long> getEmailDuplicates(String email) {
        return users.stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean doesExist(Long id) {
        return users.stream()
                .anyMatch(u -> Objects.equals(u.getId(), id));
    }
}

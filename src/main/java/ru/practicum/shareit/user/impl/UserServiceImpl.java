package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.practicum.shareit.exception.type.ExceptionType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto dto) {
        checkArgument(dto.getEmail() != null && !dto.getEmail().isBlank(), EMPTY_EMAIL.getValue());
        if (!userRepository.getEmailDuplicates(dto.getEmail()).isEmpty()) {
            throw new DuplicateException(EMAIL_DUPLICATE.getValue());
        }
        User user = userMapper.mapToModel(dto);

        log.info("creating new user");
        return userMapper.mapToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        List<Long> duplicates = userRepository.getEmailDuplicates(dto.getEmail());

        if (!duplicates.isEmpty() && !duplicates.contains(id)) {
            throw new DuplicateException(EMAIL_DUPLICATE.getValue());
        }
        User toBeUpdated = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getValue() + id));
        User user = userMapper.mapToModel(dto, toBeUpdated, id);

        log.info("updating userId = {}", id);
        return userMapper.mapToDto(userRepository.update(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> result = userRepository.getById(id);

        if (result.isPresent()) {
            log.info("getting user by id = {}", id);
            return userMapper.mapToDto(result.get());
        }
        throw new NotFoundException(USER_NOT_FOUND.getValue() + id);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.doesExist(id)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + id);
        }
        log.info("deleting user by id = {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("getting all users");
        return userRepository.getAll().stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }
}

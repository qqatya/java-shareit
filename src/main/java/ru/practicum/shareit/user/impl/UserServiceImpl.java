package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.type.ExceptionType.USER_NOT_FOUND;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto dto) {
        User user = userMapper.mapToModel(dto);

        log.info("creating new user");
        return userMapper.mapToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getValue() + id));

        user.setName(dto.getName() != null ? dto.getName() : user.getName());
        user.setEmail(dto.getEmail() != null ? dto.getEmail() : user.getEmail());
        log.info("updating userId = {}", id);
        return userMapper.mapToDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        Optional<User> result = userRepository.findById(id);

        if (result.isPresent()) {
            log.info("getting user by id = {}", id);
            return userMapper.mapToDto(result.get());
        }
        throw new NotFoundException(USER_NOT_FOUND.getValue() + id);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + id);
        }
        log.info("deleting user by id = {}", id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("getting all users");
        return userRepository.findAll().stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }
}

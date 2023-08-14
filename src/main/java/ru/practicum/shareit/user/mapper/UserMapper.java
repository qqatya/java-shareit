package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public User mapToModel(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User mapToModel(UserDto dto, User existingModel, Long id) {
        return User.builder()
                .id(id)
                .name(dto.getName() != null ? dto.getName() : existingModel.getName())
                .email(dto.getEmail() != null ? dto.getEmail() : existingModel.getEmail())
                .build();
    }
}

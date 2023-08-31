package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validation.UserCreate;
import ru.practicum.shareit.user.validation.UserUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {

    private Long id;

    @NotBlank(groups = UserCreate.class)
    private String name;

    @Email(groups = {UserCreate.class, UserUpdate.class})
    @NotBlank(groups = UserCreate.class)
    private String email;

}

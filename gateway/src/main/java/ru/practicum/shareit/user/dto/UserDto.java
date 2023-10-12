package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.validation.UserCreate;
import ru.practicum.shareit.user.validation.UserUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserDto {

    @NotBlank(groups = UserCreate.class)
    private String name;

    @Email(groups = {UserCreate.class, UserUpdate.class})
    @NotBlank(groups = UserCreate.class)
    private String email;

}

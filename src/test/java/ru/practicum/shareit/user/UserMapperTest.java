package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserMapperTest {

    private final UserMapper userMapper;

    @Test
    public void mapToModelCreatesModel() {
        UserDto dto = UserDto.builder()
                .name("Ivan")
                .email("ivan@test.com")
                .build();

        User actual = userMapper.mapToModel(dto);

        assertEquals(dto.getName(), actual.getName());
        assertEquals(dto.getEmail(), actual.getEmail());
    }

    @Test
    public void mapToDtoCreatesDto() {
        User user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@test.com")
                .build();

        UserDto actual = userMapper.mapToDto(user);

        assertEquals(user.getId(), actual.getId());
        assertEquals(user.getName(), actual.getName());
        assertEquals(user.getEmail(), actual.getEmail());
    }
}

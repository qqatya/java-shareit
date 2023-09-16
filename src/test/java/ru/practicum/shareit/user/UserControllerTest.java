package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("Ivan")
            .email("ivan@test.com")
            .build();

    @Test
    @SneakyThrows
    public void createUser() {
        UserDto expected = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@test.com")
                .build();
        Mockito.when(userService.createUser(userDto)).thenReturn(expected);

        String response = mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, UserDto.class));
    }

    @Test
    @SneakyThrows
    public void updateItem() {
        UserDto toUpdate = userDto;
        toUpdate.setName("Ivan Ivanov");
        UserDto expected = UserDto.builder()
                .name(toUpdate.getName())
                .email(toUpdate.getEmail())
                .build();
        Mockito.when(userService.updateUser(1L, userDto)).thenReturn(expected);

        String response = mockMvc.perform(patch("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, UserDto.class));
    }

    @Test
    @SneakyThrows
    public void getItemById() {
        UserDto expected = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@test.com")
                .build();
        Mockito.when(userService.getUserById(1L))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, UserDto.class));
    }

    @Test
    @SneakyThrows
    public void deleteUserById() {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getAllUsers() {
        List<UserDto> expected = List.of(userDto, userDto);
        Mockito.when(userService.getAllUsers())
                .thenReturn(expected);

        String response = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, new TypeReference<List<UserDto>>() {
        }));
    }


}

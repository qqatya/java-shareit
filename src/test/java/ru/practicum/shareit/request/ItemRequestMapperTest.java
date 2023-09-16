package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapperTest {

    @MockBean
    private final ItemMapper itemMapper;

    @InjectMocks
    private final ItemRequestMapper itemRequestMapper;

    private final ItemRequestDto dto = ItemRequestDto.builder()
            .description("Some description")
            .build();
    private final User user = User.builder()
            .id(1L)
            .name("Ivan")
            .email("ivan@test.com")
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .user(user)
            .description(dto.getDescription())
            .createDttm(LocalDateTime.now())
            .build();

    @Test
    public void testMappingToModel() {
        ItemRequest actual = itemRequestMapper.mapToModel(dto, user);

        assertEquals(user, actual.getUser());
        assertEquals(dto.getDescription(), actual.getDescription());
        assertNotNull(actual.getCreateDttm());
    }

    @Test
    public void testMappingToDto() {
        ItemRequestDto actual = itemRequestMapper.mapToDto(request);

        assertEquals(request.getId(), actual.getId());
        assertEquals(request.getDescription(), actual.getDescription());
        assertEquals(request.getCreateDttm(), actual.getCreated());

    }

    @Test
    public void testMappingToDto_withItems() {
        List<Item> items = List.of(Item.builder().id(1L).owner(user).name("Camera").description("Analog camera")
                .available(true).build(), Item.builder().id(2L).owner(user).name("Camera").description("Analog camera")
                .available(true).build());
        List<ItemDto> itemDtos = List.of(ItemDto.builder().id(1L).name("Camera").description("Analog camera")
                .available(true).build(), ItemDto.builder().id(2L).name("Camera").description("Analog camera")
                .available(true).build());
        Mockito.when(itemMapper.mapToDtos(items)).thenReturn(itemDtos);
        ItemRequestDto actual = itemRequestMapper.mapToDto(request, items);

        assertEquals(request.getId(), actual.getId());
        assertEquals(request.getDescription(), actual.getDescription());
        assertEquals(request.getCreateDttm(), actual.getCreated());
        assertEquals(itemDtos, actual.getItems());

    }

}

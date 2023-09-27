package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final ItemMapper itemMapper;

    public ItemRequest mapToModel(ItemRequestDto dto, User user) {
        return ItemRequest.builder()
                .id(dto.getId())
                .user(user)
                .description(dto.getDescription())
                .createDttm(LocalDateTime.now())
                .build();
    }

    public ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreateDttm())
                .build();
    }

    public ItemRequestDto mapToDto(ItemRequest itemRequest, List<Item> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreateDttm())
                .items(itemMapper.mapToDtos(items))
                .build();
    }

}

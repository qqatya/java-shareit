package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public Item mapToModel(ItemDto dto, Long ownerId) {
        return Item.builder()
                .id(dto.getId())
                .ownerId(ownerId)
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item mapToModel(ItemDto dto, Long id, Long ownerId, Item existingItem) {
        return Item.builder()
                .id(id)
                .ownerId(ownerId)
                .name(dto.getName() != null ? dto.getName() : existingItem.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : existingItem.getDescription())
                .available(dto.getAvailable() != null ? dto.getAvailable() : existingItem.getAvailable())
                .build();
    }

}

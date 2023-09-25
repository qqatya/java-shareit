package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.validation.ItemCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(groups = ItemCreate.class)
    private String name;

    @NotBlank(groups = ItemCreate.class)
    private String description;

    @NotNull(groups = ItemCreate.class)
    private Boolean available;

    private Long requestId;
}

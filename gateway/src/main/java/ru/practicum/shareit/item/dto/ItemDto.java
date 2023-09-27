package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.validation.ItemCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
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

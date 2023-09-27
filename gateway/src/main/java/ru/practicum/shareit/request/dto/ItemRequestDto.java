package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ItemRequestDto {

    @NotBlank
    private String description;

}

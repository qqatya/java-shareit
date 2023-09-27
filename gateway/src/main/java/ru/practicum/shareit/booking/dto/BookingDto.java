package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingDto {

    @NotNull
    private Long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

}

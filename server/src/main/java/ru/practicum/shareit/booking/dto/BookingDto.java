package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.type.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    private Long id;

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    private UserDto booker;

    private ItemDto item;
}

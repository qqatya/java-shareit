package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.type.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingInfoDto {

    private Long id;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    BookingStatus status;
}

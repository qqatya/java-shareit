package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemBookingDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingInfoDto lastBooking;

    private BookingInfoDto nextBooking;

    private List<CommentDto> comments;
}

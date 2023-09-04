package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.type.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserMapper userMapper;

    private final ItemMapper itemMapper;

    public BookingDto mapToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStartDttm())
                .end(booking.getEndDttm())
                .status(booking.getStatus())
                .booker(userMapper.mapToDto(booking.getInitiator()))
                .item(itemMapper.mapToDto(booking.getItem()))
                .build();
    }

    public Booking mapToModel(BookingDto dto, User initiator, Item item, BookingStatus status) {
        return Booking.builder()
                .item(item)
                .initiator(initiator)
                .startDttm(dto.getStart())
                .endDttm(dto.getEnd())
                .status(status)
                .build();
    }

}

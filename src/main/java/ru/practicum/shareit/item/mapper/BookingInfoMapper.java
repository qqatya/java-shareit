package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingInfoDto;

@Component
public class BookingInfoMapper {

    BookingInfoDto mapToDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingInfoDto.builder()
                .id(booking.getId())
                .bookerId(booking.getInitiator().getId())
                .start(booking.getStartDttm())
                .end(booking.getEndDttm())
                .status(booking.getStatus())
                .build();
    }
}

package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingInfoDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.model.type.BookingStatus.APPROVED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingInfoMapperTest {

    private final BookingInfoMapper bookingInfoMapper;

    @Test
    public void mapToDtoReturnsNull() {
        BookingInfoDto actual = bookingInfoMapper.mapToDto(null);

        assertNull(actual);
    }

    @Test
    public void mapToDtoCreatesDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .initiator(User.builder().id(1L).build())
                .status(APPROVED)
                .build();
        BookingInfoDto actual = bookingInfoMapper.mapToDto(booking);

        assertEquals(booking.getId(), actual.getId());
        assertEquals(booking.getInitiator().getId(), actual.getBookerId());
        assertEquals(booking.getStatus(), actual.getStatus());
    }
}

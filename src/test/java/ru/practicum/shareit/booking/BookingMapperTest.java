package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.type.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapperTest {

    @MockBean
    private final UserMapper userMapper;

    @MockBean
    private final ItemMapper itemMapper;

    @InjectMocks
    private final BookingMapper bookingMapper;

    private final User initiator = User.builder()
            .id(1L)
            .name("Ivan")
            .email("ivan@test.com")
            .build();
    private final User owner = User.builder()
            .id(2L)
            .name("Arkadiy")
            .email("arkadiy@test.com")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .owner(owner)
            .name("Camera")
            .description("Analog camera")
            .available(true)
            .build();

    @Test
    public void mapToDtoCreatesDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .initiator(initiator)
                .startDttm(LocalDateTime.now())
                .endDttm(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();
        UserDto userDto = UserDto.builder()
                .id(initiator.getId())
                .name(initiator.getName())
                .email(initiator.getEmail())
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        Mockito.when(userMapper.mapToDto(booking.getInitiator())).thenReturn(userDto);
        Mockito.when(itemMapper.mapToDto(booking.getItem())).thenReturn(itemDto);

        BookingDto result = bookingMapper.mapToDto(booking);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getItemId());
        assertEquals(booking.getStartDttm(), result.getStart());
        assertEquals(booking.getEndDttm(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(userDto, result.getBooker());
        assertEquals(itemDto, result.getItem());
    }

    @Test
    public void mapToModelCreatesModel() {
        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Booking actual = bookingMapper.mapToModel(dto, initiator, item, BookingStatus.WAITING);

        assertEquals(dto.getStart(), actual.getStartDttm());
        assertEquals(dto.getEnd(), actual.getEndDttm());
        assertEquals(initiator, actual.getInitiator());
        assertEquals(item, actual.getItem());
        assertEquals(BookingStatus.WAITING, actual.getStatus());
    }
}

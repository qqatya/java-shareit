package ru.practicum.shareit.item.mapper;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemMapperTest {

    @MockBean
    private final CommentMapper commentMapper;

    @MockBean
    private final BookingMapper bookingMapper;

    @InjectMocks
    private final ItemMapper itemMapper;

    private final User author = User.builder()
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
    private final ItemDto itemDto = ItemDto.builder()
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .build();

    @Test
    public void mapToDto() {
        ItemDto actual = itemMapper.mapToDto(item);

        assertEquals(actual.getId(), item.getId());
        assertEquals(actual.getName(), item.getName());
        assertEquals(actual.getDescription(), item.getDescription());
        assertEquals(actual.getAvailable(), item.getAvailable());
    }

    @Test
    public void mapToModel_withItemRequest() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .user(author)
                .description("Desc")
                .createDttm(LocalDateTime.now())
                .build();

        Item actual = itemMapper.mapToModel(itemDto, author, request);

        assertEquals(actual.getName(), itemDto.getName());
        assertEquals(actual.getOwner(), author);
        assertEquals(actual.getDescription(), itemDto.getDescription());
        assertEquals(actual.getAvailable(), itemDto.getAvailable());
        assertEquals(actual.getRequest(), request);
    }

    @Test
    public void mapToModel() {
        ItemDto toUpdate = itemDto;
        toUpdate.setName("Upd");
        toUpdate.setAvailable(false);

        Item actual = itemMapper.mapToModel(item, toUpdate);

        assertEquals(actual.getName(), toUpdate.getName());
        assertEquals(actual.getAvailable(), toUpdate.getAvailable());
    }

    @Test
    public void mapToItemBookingDto() {
        List<Comment> comments = List.of(Comment.builder().id(1L).text("Some text").item(item).author(author)
                        .createDttm(LocalDateTime.now()).build(),
                Comment.builder().id(2L).text("Some text").item(item).author(author)
                        .createDttm(LocalDateTime.now()).build());
        List<CommentDto> commentDtos = List.of(CommentDto.builder().id(1L)
                        .text("Some text").itemId(item.getId()).authorName(author.getName()).build(),
                CommentDto.builder().id(2L).text("Some text").itemId(item.getId())
                        .authorName(author.getName()).build());
        Mockito.when(commentMapper.mapToDtos(comments)).thenReturn(commentDtos);

        ItemBookingDto actual = itemMapper.mapToItemBookingDto(item, comments);

        assertEquals(actual.getName(), item.getName());
        assertEquals(actual.getDescription(), item.getDescription());
        assertEquals(actual.getAvailable(), item.getAvailable());
        assertEquals(actual.getComments(), commentDtos);
    }

    @Test
    public void mapToItemBookingDto_withBookings() {
        List<Comment> comments = List.of(Comment.builder().id(1L).text("Some text").item(item).author(author)
                        .createDttm(LocalDateTime.now()).build(),
                Comment.builder().id(2L).text("Some text").item(item).author(author)
                        .createDttm(LocalDateTime.now()).build());
        List<CommentDto> commentDtos = List.of(CommentDto.builder().id(1L)
                        .text("Some text").itemId(item.getId()).authorName(author.getName()).build(),
                CommentDto.builder().id(2L).text("Some text").itemId(item.getId())
                        .authorName(author.getName()).build());
        Booking lastBooking = Booking.builder().id(1L).item(item).initiator(author)
                .startDttm(LocalDateTime.now().minusDays(2)).endDttm(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.WAITING).build();
        BookingDto lastBookingDto = BookingDto.builder().id(1L).itemId(1L).start(lastBooking.getStartDttm())
                .end(lastBooking.getEndDttm()).build();
        Booking nextBooking = Booking.builder().id(2L).item(item).initiator(author).startDttm(LocalDateTime.now())
                .endDttm(LocalDateTime.now().plusDays(1)).status(BookingStatus.WAITING).build();
        BookingDto nextBookingDto = BookingDto.builder().id(2L).itemId(1L).start(lastBooking.getStartDttm())
                .end(lastBooking.getEndDttm()).build();
        Mockito.when(commentMapper.mapToDtos(comments)).thenReturn(commentDtos);
        Mockito.when(bookingMapper.mapToDto(lastBooking)).thenReturn(lastBookingDto);
        Mockito.when(bookingMapper.mapToDto(nextBooking)).thenReturn(nextBookingDto);

        ItemBookingDto actual = itemMapper.mapToItemBookingDto(item, comments, lastBooking, nextBooking);

        assertEquals(actual.getName(), item.getName());
        assertEquals(actual.getDescription(), item.getDescription());
        assertEquals(actual.getAvailable(), item.getAvailable());
        assertEquals(actual.getComments(), commentDtos);
        assertNotNull(actual.getLastBooking());
        assertNotNull(actual.getNextBooking());
    }
}

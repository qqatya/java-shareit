package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.practicum.shareit.booking.model.type.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.type.BookingStatus.WAITING;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final ItemMapper itemMapper;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentMapper commentMapper;

    @MockBean
    private final CommentRepository commentRepository;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private final ItemService itemService;

    private final User user1 = User.builder()
            .id(1L)
            .name("Ivan")
            .email("ivan@test.com")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("Arkadiy")
            .email("arkadiy@test.com")
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Camera")
            .description("Analog camera")
            .available(true)
            .build();

    private final Item item1 = Item.builder()
            .id(1L)
            .owner(user1)
            .name("Camera")
            .description("Analog camera")
            .available(true)
            .build();

    private final Item item2 = Item.builder()
            .id(1L)
            .owner(user1)
            .name("Sweater")
            .description("Red sweater")
            .available(true)
            .build();

    private final Booking booking1 = Booking.builder()
            .id(1L)
            .status(APPROVED)
            .build();

    private final Booking booking2 = Booking.builder()
            .id(2L)
            .status(WAITING)
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .text("Comment text")
            .itemId(1L)
            .build();

    private final Comment comment = Comment.builder()
            .text(commentDto.getText())
            .item(item1)
            .author(user2)
            .createDttm(LocalDateTime.now())
            .build();

    @Test
    public void createItemSavesItem() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(itemMapper.mapToModel(itemDto, user1, null))
                .thenReturn(item1);

        itemService.createItem(itemDto, 1L);

        verify(itemRepository, times(1)).save(item1);
    }

    @Test
    public void createItemThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 1L));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void createItemThrowsNotFoundExceptionWhenRequestNotFound() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemDto withRequestId = itemDto;
        withRequestId.setRequestId(1L);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.createItem(withRequestId, 1L));
        assertEquals("Не найден запрос вещи с id = 1", e.getMessage());
    }

    @Test
    public void updateItemUpdatesItem() {
        Item itemUpdated = item1;
        itemUpdated.setName("Updated camera");
        itemUpdated.setAvailable(false);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        Mockito.when(itemMapper.mapToModel(item1, itemDto)).thenReturn(itemUpdated);

        itemService.updateItem(1L, 1L, itemDto);

        verify(itemRepository, times(1)).save(itemUpdated);
    }

    @Test
    public void updateItemThrowsSecurityExceptionWhenUpdatedNotByOwner() {
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        SecurityException e = assertThrows(SecurityException.class,
                () -> itemService.updateItem(2L, 1L, itemDto));
        assertEquals("Пользователь не является владельцем вещи с id = 1", e.getMessage());
    }

    @Test
    public void updateItemThrowsNotFoundExceptionWhenItemNotFound() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, itemDto));
        assertEquals("Не найдена вещь с id = 1", e.getMessage());
    }

    @Test
    public void getItemByIdReturnsItem() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        Mockito.when(commentRepository.findByItemId(item1.getId())).thenReturn(List.of());

        itemService.getItemById(1L, 2L);
        verify(itemRepository, times(1)).findById(1L);
        verify(itemMapper, times(1)).mapToItemBookingDto(item1, List.of());
    }

    @Test
    public void getItemByIdReturnsTheItemWithLastAndNextBookingWhenRequestedByOwner() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        Mockito.when(commentRepository.findByItemId(item1.getId())).thenReturn(List.of());
        Mockito.when(bookingRepository.findLastByItemId(anyLong()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(bookingRepository.findNextByItemId(anyLong()))
                .thenReturn(Optional.of(booking2));

        itemService.getItemById(1L, 1L);
        verify(itemRepository, times(1)).findById(1L);
        verify(itemMapper, times(1)).mapToItemBookingDto(item1, List.of(), booking1, booking2);
    }

    @Test
    public void getItemByIdThrowsNotFoundExceptionWhenItemNotFound() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1L, 1L));
        assertEquals("Не найдена вещь с id = 1", e.getMessage());
    }

    @Test
    public void getItemsReturnsItems() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(item1, item2));
        Mockito.when(commentRepository.findByItemId(anyLong())).thenReturn(List.of());
        Mockito.when(bookingRepository.findLastByItemId(anyLong()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(bookingRepository.findNextByItemId(anyLong()))
                .thenReturn(Optional.of(booking2));

        itemService.getItems(1L, 1, 0);

        verify(itemRepository, times(1))
                .getByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 1));
        verify(itemMapper, times(1)).mapToItemBookingDto(item1, List.of(), booking1, booking2);
        verify(itemMapper, times(1)).mapToItemBookingDto(item2, List.of(), booking1, booking2);
    }

    @Test
    public void getItemsThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getItems(1L, 1, 0));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());

    }

    @Test
    public void createCommentCreatesComment() {
        Mockito.when(bookingRepository.wasItemBookedByUser(anyLong(), anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        Mockito.when(commentMapper.mapToModel(commentDto, item1, user2)).thenReturn(comment);

        itemService.createComment(commentDto, 1L, 2L);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    public void createCommentThrowsIllegalArgumentExceptionWhenWasntBookedByAuthor() {
        Mockito.when(bookingRepository.wasItemBookedByUser(anyLong(), anyLong())).thenReturn(false);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> itemService.createComment(commentDto, 1L, 2L));
        assertEquals("Пользователь с id = 2 не бронировал эту вещь", e.getMessage());
    }

    @Test
    public void createCommentThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(bookingRepository.wasItemBookedByUser(anyLong(), anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentDto, 1L, 2L));
        assertEquals("Не найден пользователь с id = 2", e.getMessage());
    }

    @Test
    public void createCommentThrowsIllegalArgumentExceptionWhenItemNotFound() {
        Mockito.when(itemRepository.existsById(anyLong())).thenReturn(false);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> itemService.createComment(commentDto, 1L, 2L));
        assertEquals("Пользователь с id = 2 не бронировал эту вещь", e.getMessage());
    }

    @Test
    public void searchItemsReturnsItems() {
        itemService.searchItems("Item", 1, 0);

        verify(itemRepository, times(1))
                .getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("Item", "Item",
                        PageRequest.of(0, 1));
    }

    @Test
    public void searchItemsReturnsEmptyListWhenTextIsEmpty() {
        List<ItemDto> result = itemService.searchItems("", 1, 0);

        assertTrue(result.isEmpty());
    }
}

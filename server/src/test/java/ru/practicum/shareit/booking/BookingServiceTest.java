package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.type.BookingStatus;
import ru.practicum.shareit.exception.BookingPeriodException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.practicum.shareit.booking.model.type.BookingSearchType.*;
import static ru.practicum.shareit.booking.model.type.BookingStatus.APPROVED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final BookingMapper bookingMapper;

    @MockBean
    private final BookingRepository bookingRepository;

    @InjectMocks
    private final BookingService bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

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
    private final Item item = Item.builder()
            .id(1L)
            .owner(user1)
            .name("Camera")
            .description("Analog camera")
            .available(true)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(2))
            .end(LocalDateTime.now().plusDays(3))
            .build();

    private final Booking booking = Booking.builder()
            .item(item)
            .initiator(user2)
            .startDttm(bookingDto.getStart())
            .endDttm(bookingDto.getEnd())
            .status(BookingStatus.WAITING)
            .build();

    @Test
    public void createSavesBooking() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findIntersections(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());
        Mockito.when(bookingMapper.mapToModel(any(BookingDto.class), any(User.class), any(Item.class), any(BookingStatus.class)))
                .thenReturn(booking);

        bookingService.create(bookingDto, 2L);

        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    public void createThrowsNotFoundException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, 1L));
        assertEquals("Владелец вещи не может ее забронировать", e.getMessage());
    }

    @Test
    public void createThrowsBookingPeriodExceptionWhenStartIsBeforeNow() {
        BookingDto startBeforeNow = bookingDto;
        startBeforeNow.setStart(LocalDateTime.now().minusMinutes(1));

        BookingPeriodException e = assertThrows(BookingPeriodException.class,
                () -> bookingService.create(startBeforeNow, 1L));
        assertEquals("Некорректно выбран период бронирования", e.getMessage());
    }

    @Test
    public void createThrowsBookingPeriodExceptionWhenEndIsBeforeStart() {
        BookingDto endBeforeStart = bookingDto;
        endBeforeStart.setEnd(endBeforeStart.getStart().minusMinutes(1));

        BookingPeriodException e = assertThrows(BookingPeriodException.class,
                () -> bookingService.create(endBeforeStart, 1L));
        assertEquals("Некорректно выбран период бронирования", e.getMessage());
    }

    @Test
    public void createThrowsBookingPeriodExceptionWhenEndEqualsStart() {
        BookingDto endEqStart = bookingDto;
        endEqStart.setEnd(endEqStart.getStart());

        BookingPeriodException e = assertThrows(BookingPeriodException.class,
                () -> bookingService.create(endEqStart, 1L));
        assertEquals("Некорректно выбран период бронирования", e.getMessage());
    }

    @Test
    public void createThrowsNotFoundExceptionWhenHasIntersectionsByBookingPeriod() {
        Mockito.when(bookingRepository.findIntersections(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, 2L));
        assertEquals("Некорректно выбран период бронирования", e.getMessage());
    }

    @Test
    public void createThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, 2L));
        assertEquals("Не найден пользователь с id = 2", e.getMessage());
    }

    @Test
    public void createThrowsNotFoundExceptionWhenItemNotFound() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, 2L));
        assertEquals("Не найдена вещь с id = 1", e.getMessage());
    }

    @Test
    public void changeStatusChangesStatusToApproved() {
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
                .thenReturn(Optional.of(Booking.builder().initiator(user2).item(item).build()));

        bookingService.changeStatus(1L, true, 1L);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(APPROVED, savedBooking.getStatus());
    }

    @Test
    public void changeStatusChangesStatusToRejected() {
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
                .thenReturn(Optional.of(Booking.builder().initiator(user2).build()));

        bookingService.changeStatus(1L, false, 1L);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(BookingStatus.REJECTED, savedBooking.getStatus());
    }

    @Test
    public void changeStatusThrowsNotFoundExceptionWhenBookingNotFound() {
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.changeStatus(1L, true, 1L));
        assertEquals("Не найдено бронирование с id = 1", e.getMessage());
    }

    @Test
    public void changeStatusThrowsUnsupportedOperationExceptionWhenTryingToApproveAlreadyApprovedBooking() {
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
                .thenReturn(Optional.of(Booking.builder().status(APPROVED).initiator(user2).build()));

        UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class,
                () -> bookingService.changeStatus(1L, true, 1L));
        assertEquals("Бронирование с id 1 уже одобрено", e.getMessage());
    }

    @Test
    public void getBookingByIdReturnsTheBooking() {
        Mockito.when(bookingRepository.findByIdAndItemOwnerIdOrInitiatorId(anyLong(), anyLong()))
                .thenReturn(Optional.of(Booking.builder().status(APPROVED).initiator(user2).build()));

        bookingService.getBookingById(1L, 2L);

        verify(bookingRepository, times(1))
                .findByIdAndItemOwnerIdOrInitiatorId(1L, 2L);
    }

    @Test
    public void getBookingByIdThrowsNotFoundExceptionWhenBookingNotFound() {
        Mockito.when(bookingRepository.findByIdAndItemOwnerIdOrInitiatorId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, 2L));
        assertEquals("Не найдено бронирование с id = 1", e.getMessage());
    }

    @Test
    public void getBookingsByUserIdSearchingByAll() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        bookingService.getBookingsByUserId(ALL, 1L, 1, 0);

        verify(bookingRepository, times(1))
                .findByInitiatorIdOrderByStartDttmDesc(1L,
                        PageRequest.of(0, 1, Sort.by("startDttm").descending()));
    }

    @Test
    public void getBookingsByUserIdSearchingByCurrent() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByUserId(CURRENT, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByUserIdSearchingByPast() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByUserId(PAST, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByUserIdSearchingByFuture() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByUserId(FUTURE, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByUserIdSearchingByWaiting() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByUserId(WAITING, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByUserIdSearchingByRejected() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByUserId(REJECTED, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByUserIdThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByUserId(CURRENT, 1L, 1, 0));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void getBookingsByItemOwnerSearchingByAll() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        bookingService.getBookingsByItemOwner(ALL, 1L, 1, 0);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdOrderByStartDttmDesc(1L,
                        PageRequest.of(0, 1, Sort.by("startDttm").descending()));
    }

    @Test
    public void getBookingsByItemOwnerSearchingByCurrent() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByItemOwner(CURRENT, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByItemOwnerSearchingByPast() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByItemOwner(PAST, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByItemOwnerSearchingByFuture() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByItemOwner(FUTURE, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByItemOwnerSearchingByWaiting() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByItemOwner(WAITING, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByItemOwnerSearchingByRejected() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        bookingService.getBookingsByItemOwner(REJECTED, userId, 1, 0);

        verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    public void getBookingsByItemOwnerThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByItemOwner(CURRENT, 1L, 1, 0));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void getBookingsByUserIdThrowsNotFoundExceptionWhenSearchTypeNotFound() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByUserId(TEST, userId, 1, 0));
        assertEquals("Статус TEST не доступен для обработки", e.getMessage());
    }

    @Test
    public void getBookingsByItemOwnerThrowsNotFoundExceptionWhenSearchTypeNotFound() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        Long userId = 1L;

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByItemOwner(TEST, userId, 1, 0));
        assertEquals("Статус TEST не доступен для обработки", e.getMessage());
    }

}

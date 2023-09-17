package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.type.BookingSearchType;
import ru.practicum.shareit.exception.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.shareit.exception.type.ExceptionType.UNSUPPORTED_STATE;
import static ru.practicum.shareit.util.Header.SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    /**
     * Создание бронирования
     *
     * @param dto         Объект, содержащий данные для создания
     * @param initiatorId Идентификатор инициатора
     * @return Созданное бронирование
     */
    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto dto,
                             @RequestHeader(SHARER_USER_ID) Long initiatorId) {
        return bookingService.create(dto, initiatorId);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование
     *
     * @param bookingId Идентификатор бронирования
     * @param approved  Признак одобрения
     * @param userId    Идентификатор пользователя
     * @return Бронирование с обновленным признаком
     */
    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@PathVariable Long bookingId,
                                   @Valid @NotNull @RequestParam Boolean approved,
                                   @RequestHeader(SHARER_USER_ID) Long userId) {
        return bookingService.changeStatus(bookingId, approved, userId);
    }

    /**
     * Получение данных о бронировании по идентификатору
     *
     * @param bookingId Идентификатор бронирования
     * @param userId    Идентификатор пользователя
     * @return Бронирование
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(SHARER_USER_ID) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    /**
     * Постраничное получение списка всех бронирований текущего пользователя
     *
     * @param state  Статус бронирований
     * @param userId Идентификатор пользователя
     * @param size   Количество элементов для отображения
     * @param from   Индекс первого элемента
     * @return Список бронирований
     */
    @GetMapping
    public List<BookingDto> getBookingsByUserId(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader(SHARER_USER_ID) Long userId,
                                                @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size,
                                                @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from) {
        try {
            return bookingService.getBookingsByUserId(BookingSearchType.valueOf(state), userId, size, from);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException(String.format(UNSUPPORTED_STATE.getValue(), state));
        }
    }

    /**
     * Постраничное получение списка бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param state   Статус бронирований
     * @param ownerId Идентификатор владельца
     * @param size    Количество элементов для отображения
     * @param from    Индекс первого элемента
     * @return Список бронирований
     */
    @GetMapping("/owner")
    public List<BookingDto> getBookingsByItemOwner(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader(SHARER_USER_ID) Long ownerId,
                                                   @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size,
                                                   @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from) {
        try {
            return bookingService.getBookingsByItemOwner(BookingSearchType.valueOf(state), ownerId, size, from);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException(String.format(UNSUPPORTED_STATE.getValue(), state));
        }
    }
}

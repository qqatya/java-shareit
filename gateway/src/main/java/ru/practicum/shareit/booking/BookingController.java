package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSearchType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Header.SHARER_USER_ID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    /**
     * Создание бронирования
     *
     * @param dto    Объект, содержащий данные для создания
     * @param userId Идентификатор инициатора
     * @return Созданное бронирование
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(SHARER_USER_ID) Long userId,
                                         @Valid @RequestBody BookingDto dto) {
        log.info("Creating booking {}, userId = {}", dto, userId);
        return bookingClient.create(userId, dto);
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
    public ResponseEntity<Object> changeStatus(@PathVariable Long bookingId,
                                               @Valid @NotNull @RequestParam Boolean approved,
                                               @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Change status for bookingId = {}, approved = {}, userId = {}", bookingId, approved, userId);
        return bookingClient.changeStatus(bookingId, approved, userId);
    }

    /**
     * Получение данных о бронировании по идентификатору
     *
     * @param bookingId Идентификатор бронирования
     * @param userId    Идентификатор пользователя
     * @return Бронирование
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(SHARER_USER_ID) Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get bookingId = {}, userId = {}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    /**
     * Постраничное получение списка всех бронирований текущего пользователя
     *
     * @param stateParam Статус бронирований
     * @param userId     Идентификатор пользователя
     * @param size       Количество элементов для отображения
     * @param from       Индекс первого элемента
     * @return Список бронирований
     */
    @GetMapping
    public ResponseEntity<Object> getBookingsByUserId(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @RequestHeader(SHARER_USER_ID) Long userId,
                                                      @PositiveOrZero @RequestParam(required = false,
                                                              defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(required = false,
                                                              defaultValue = "10") Integer size) {
        BookingSearchType state = BookingSearchType.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings with state = {}, userId = {}, from = {}, size = {}", stateParam, userId, from, size);
        return bookingClient.getBookingsByUserId(userId, state, from, size);
    }

    /**
     * Постраничное получение списка бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param stateParam Статус бронирований
     * @param ownerId    Идентификатор владельца
     * @param size       Количество элементов для отображения
     * @param from       Индекс первого элемента
     * @return Список бронирований
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByItemOwner(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                         @RequestHeader(SHARER_USER_ID) Long ownerId,
                                                         @PositiveOrZero @RequestParam(required = false,
                                                                 defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(required = false,
                                                                 defaultValue = "10") Integer size) {
        BookingSearchType state = BookingSearchType.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings with state = {}, ownerId = {}, from = {}, size = {}", stateParam, ownerId, from, size);
        return bookingClient.getBookingsByItemOwner(ownerId, state, from, size);
    }
}
package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.type.BookingSearchType;

import java.util.List;

public interface BookingService {

    /**
     * Создание бронирования
     *
     * @param dto         Объект, содержащий данные для создания
     * @param initiatorId Идентификатор инициатора
     * @return Созданное бронирование
     */
    BookingDto create(BookingDto dto, Long initiatorId);

    /**
     * Подтверждение или отклонение запроса на бронирование
     *
     * @param bookingId Идентификатор бронирования
     * @param approved  Признак одобрения
     * @param ownerId   Идентификатор владельца
     * @return Бронирование с обновленным признаком
     */
    BookingDto changeStatus(Long bookingId, Boolean approved, Long ownerId);

    /**
     * Получение данных о бронировании по идентификатору
     *
     * @param bookingId Идентификатор бронирования
     * @param userId    Идентификатор пользователя
     * @return Бронирование
     */
    BookingDto getBookingById(Long bookingId, Long userId);

    /**
     * Получение списка всех бронирований текущего пользователя
     *
     * @param state  Статус бронирований
     * @param userId Идентификатор пользователя
     * @return Список бронирований
     */
    List<BookingDto> getBookingsByUserId(BookingSearchType state, Long userId);

    /**
     * Получение списка бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param state   Статус бронирований
     * @param ownerId Идентификатор владельца
     * @return Список бронирований
     */
    List<BookingDto> getBookingsByItemOwner(BookingSearchType state, Long ownerId);
}

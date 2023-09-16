package ru.practicum.shareit.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {

    USER_NOT_FOUND("Не найден пользователь с id = "),
    ITEM_NOT_FOUND("Не найдена вещь с id = "),
    EDITING_DENIED("Пользователь не является владельцем вещи с id = "),
    BOOKING_NOT_FOUND("Не найдено бронирование с id = "),
    INVALID_BOOKING_PERIOD("Некорректно выбран период бронирования"),
    INVALID_BOOKING_STATE("Статус %s не доступен для обработки"),
    UNSUPPORTED_STATE("Unknown state: %s"),
    ALREADY_APPROVED("Бронирование с id %s уже одобрено"),
    BOOKING_BY_OWNER("Владелец вещи не может ее забронировать"),
    WASNT_BOOKED_BY_USER("Пользователь с id = %s не бронировал эту вещь"),
    ITEM_REQUEST_NOT_FOUND("Не найден запрос вещи с id = ");

    private final String value;
}

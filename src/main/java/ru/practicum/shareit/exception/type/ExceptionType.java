package ru.practicum.shareit.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {

    USER_NOT_FOUND("Не найден пользователь с id = "),
    EMPTY_EMAIL("Не заполнена электронная почта"),
    ITEM_NOT_FOUND("Не найдена вещь с id = "),
    EDITING_DENIED("Пользователь не является владельцем вещи с id = "),
    EMPTY_ITEM_NAME("Название вещи не может быть пустым"),
    EMPTY_ITEM_DESCRIPTION("Описание вещи не может быть пустым"),
    EMPTY_ITEM_AVAILABILITY("Статус доступа к аренде не может быть пустым"),
    BOOKING_NOT_FOUND("Не найдено бронирование с id = "),
    INVALID_BOOKING_PERIOD("Некорректно выбран период бронирования"),
    INVALID_BOOKING_STATE("Статус %s не доступен для обработки"),
    UNSUPPORTED_STATE("Unknown state: %s"),
    ALREADY_APPROVED("Бронирование с id %s уже одобрено"),
    BOOKING_BY_OWNER("Владелец вещи не может ее забронировать");

    private final String value;
}

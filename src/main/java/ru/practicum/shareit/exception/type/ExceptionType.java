package ru.practicum.shareit.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {

    USER_NOT_FOUND("Не найден пользователь с id = "),
    USER_DUPLICATE("Пользователь с таким id уже существует"),
    EMAIL_DUPLICATE("Пользователь с такой электронной почтой уже существует"),
    EMPTY_EMAIL("Не заполнена электронная почта"),
    ITEM_NOT_FOUND("Не найдена вещь с id = "),
    EDITING_DENIED("Пользователь не является владельцем вещи с id = "),
    EMPTY_ITEM_NAME("Название вещи не может быть пустым"),
    EMPTY_ITEM_DESCRIPTION("Описание вещи не может быть пустым"),
    EMPTY_ITEM_AVAILABILITY("Статус доступа к аренде не может быть пустым");

    private final String value;
}

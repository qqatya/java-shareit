package ru.practicum.shareit.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {

    USER_NOT_FOUND("Не найден пользователь с id = "),
    USER_DUPLICATE("Пользователь с таким id уже существует"),
    EMAIL_DUPLICATE("Пользователь с такой электронной почтой уже существует"),
    EMPTY_EMAIL("Не заполнена электронная почта");


    private final String value;
}

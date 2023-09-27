package ru.practicum.shareit.exception;

import static ru.practicum.shareit.exception.type.ExceptionType.INVALID_BOOKING_PERIOD;

public class BookingPeriodException extends RuntimeException {
    public BookingPeriodException() {
        super(INVALID_BOOKING_PERIOD.getValue());
    }
}

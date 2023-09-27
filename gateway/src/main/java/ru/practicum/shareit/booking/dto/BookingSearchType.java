package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingSearchType {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static Optional<BookingSearchType> from(String stringState) {
        for (BookingSearchType state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}

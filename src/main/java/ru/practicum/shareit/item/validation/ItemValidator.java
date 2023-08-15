package ru.practicum.shareit.item.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.exception.type.ExceptionType.*;

@Component
@Slf4j
public class ItemValidator {

    /**
     * Валидация входящего объекта вещи
     *
     * @param dto Объект, содержащие данные о вещи
     */
    public void validate(ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException(EMPTY_ITEM_NAME.getValue());
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException(EMPTY_ITEM_DESCRIPTION.getValue());
        }
        if (dto.getAvailable() == null) {
            throw new IllegalArgumentException(EMPTY_ITEM_AVAILABILITY.getValue());
        }
        log.info("item is valid");
    }
}

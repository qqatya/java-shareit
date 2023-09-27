package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Header.SHARER_USER_ID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    /**
     * Создание запроса вещи
     *
     * @param userId Идентификатор создателя запроса
     * @param dto    Объект, содержащий данные о запросе
     * @return Созданный запрос
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(SHARER_USER_ID) Long userId,
                                         @Valid @RequestBody ItemRequestDto dto) {
        log.info("Creating item request {}, userId = {}", dto, userId);
        return itemRequestClient.createItemRequest(userId, dto);
    }

    /**
     * Получение списка запросов пользователя
     *
     * @param userId Идентификатор пользователя
     * @return Список запросов
     */
    @GetMapping
    public ResponseEntity<Object> getItemRequestsOfCurrentUser(@RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Get item requests of userId = {}", userId);
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    /**
     * Получение списка запросов других пользователей
     *
     * @param userId Идентификатор пользователя
     * @param size   Количество элементов для отображения
     * @param from   Индекс первого элемента
     * @return Список запросов
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsOfOtherUsers(@RequestHeader(SHARER_USER_ID) Long userId,
                                                              @PositiveOrZero @RequestParam(required = false,
                                                                      defaultValue = "0") Integer from,
                                                              @Positive @RequestParam(required = false,
                                                                      defaultValue = "10") Integer size) {
        log.info("Get item requests of other users, userId = {}, from = {}, size = {}", userId, from, size);
        return itemRequestClient.getItemRequestsOfOtherUsers(userId, size, from);
    }

    /**
     * Получение запроса вещи по идентификатору
     *
     * @param userId    Идентификатор пользователя
     * @param requestId Идентификатор запроса
     * @return Запрос вещи
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(SHARER_USER_ID) Long userId,
                                                     @PathVariable Long requestId) {
        log.info("Get item requests by id = {}, userId = {}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}

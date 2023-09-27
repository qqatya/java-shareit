package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static ru.practicum.shareit.util.Header.SHARER_USER_ID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    /**
     * Создание запроса вещи
     *
     * @param userId Идентификатор создателя запроса
     * @param dto    Объект, содержащий данные о запросе
     * @return Созданный запрос
     */
    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                            @RequestBody ItemRequestDto dto) {
        return itemRequestService.createItemRequest(userId, dto);
    }

    /**
     * Получение списка запросов пользователя
     *
     * @param userId Идентификатор пользователя
     * @return Список запросов
     */
    @GetMapping
    public List<ItemRequestDto> getItemRequestsOfCurrentUser(@RequestHeader(SHARER_USER_ID) Long userId) {
        return itemRequestService.getItemRequestsByUserId(userId);
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
    public List<ItemRequestDto> getItemRequestsOfOtherUsers(@RequestHeader(SHARER_USER_ID) Long userId,
                                                            @RequestParam Integer size, @RequestParam Integer from) {
        return itemRequestService.getItemRequestsOfOtherUsers(userId, size, from);
    }

    /**
     * Получение запроса вещи по идентификатору
     *
     * @param userId    Идентификатор пользователя
     * @param requestId Идентификатор запроса
     * @return Запрос вещи
     */
    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(SHARER_USER_ID) Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}

package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    /**
     * Создание запроса вещи
     *
     * @param userId Идентификатор создателя запроса
     * @param dto    Объект, содержащий данные о запросе
     * @return Созданный запрос
     */
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto dto);

    /**
     * Получение списка запросов пользователя
     *
     * @param userId Идентификатор пользователя
     * @return Список запросов
     */
    List<ItemRequestDto> getItemRequestsByUserId(Long userId);

    /**
     * Получение списка запросов других пользователей
     *
     * @param userId Идентификатор пользователя
     * @param size   Количество элементов для отображения
     * @param from   Индекс первого элемента
     * @return Список запросов
     */
    List<ItemRequestDto> getItemRequestsOfOtherUsers(Long userId, Integer size, Integer from);

    /**
     * Получение запроса вещи по идентификатору
     *
     * @param userId    Идентификатор пользователя
     * @param requestId Идентификатор запроса
     * @return Запрос вещи
     */
    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}

package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    /**
     * Создание вещи
     *
     * @param dto     Объект, содержащий данные для создания
     * @param ownerId Идентификатор владельца
     * @return Созданная вещь
     */
    ItemDto createItem(ItemDto dto, Long ownerId);

    /**
     * Обновление вещи
     *
     * @param ownerId Идентификатор владельца
     * @param id      Идентификатор вещи
     * @param dto     Объект, содержащий данные для обновления
     * @return Обновленная вещь
     */
    ItemDto updateItem(Long ownerId, Long id, ItemDto dto);

    /**
     * Получение вещи по идентификатору
     *
     * @param id Идентификатор вещи
     * @return Вещь
     */
    ItemDto getItemById(Long id);

    /**
     * Получение всех вещей по идентификатору владельца
     *
     * @param ownerId Идентификатор владельца
     * @return Список вещей
     */
    List<ItemDto> getAllItems(Long ownerId);

}

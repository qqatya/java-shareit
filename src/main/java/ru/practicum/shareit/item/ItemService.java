package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
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
     * @param id     Идентификатор вещи
     * @param userId Идентификатор пользователя
     * @return Вещь
     */
    ItemBookingDto getItemById(Long id, Long userId);

    /**
     * Постраничное получение всех вещей по идентификатору владельца
     *
     * @param ownerId Идентификатор владельца
     * @param size    Количество элементов для отображения
     * @param from    Индекс первого элемента
     * @return Список вещей
     */
    List<ItemBookingDto> getItems(Long ownerId, Integer size, Integer from);

    /**
     * Постраничный поиск вещей
     *
     * @param text Текст для поиска по названию/описанию
     * @param size Количество элементов для отображения
     * @param from Индекс первого элемента
     * @return Список найденных вещей
     */
    List<ItemDto> searchItems(String text, Integer size, Integer from);

    /**
     * Создание комментария
     *
     * @param dto      Объект, содержащий текст комментария
     * @param itemId   Идентификатор вещи
     * @param authorId Идентификатор автора
     * @return Объект, содержащий созданный комментарий
     */
    CommentDto createComment(CommentDto dto, Long itemId, Long authorId);
}

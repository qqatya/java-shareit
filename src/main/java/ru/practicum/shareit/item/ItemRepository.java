package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    /**
     * Создание вещи
     *
     * @param item Объект, содержащий данные для создания
     * @return Созданная вещь
     */
    Item save(Item item);

    /**
     * Обновление вещи
     *
     * @param item Объект, содержащий данные для обновления
     * @return Обновленная вещь
     */
    Item update(Item item);

    /**
     * Получение вещи по идентификатору
     *
     * @param id Идентификатор вещи
     * @return Вещь (пустой, если такой вещи нет)
     */
    Optional<Item> getById(Long id);

    /**
     * Получение всех вещей по идентификатору владельца
     *
     * @param ownerId Идентификатор владельца
     * @return Список вещей
     */
    List<Item> getByOwnerId(Long ownerId);
}

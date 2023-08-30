package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Получение всех вещей по идентификатору владельца
     *
     * @param ownerId Идентификатор владельца
     * @return Список вещей
     */
    List<Item> getByOwnerId(Long ownerId);

    /**
     * Получение всех вещей, содержащих в названии/описании текст из аргумента метода
     *
     * @param name        Текст для поиска по названию
     * @param description Текст для поиска по описанию
     * @return Список вещей
     */
    List<Item> getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);
}

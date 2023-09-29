package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Постраничное получение вещей по идентификатору владельца
     *
     * @param ownerId  Идентификатор владельца
     * @param pageable Параметры пагинации
     * @return Список вещей
     */
    List<Item> getByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);

    /**
     * Получение всех вещей, содержащих в названии/описании текст из аргумента метода
     *
     * @param name        Текст для поиска по названию
     * @param description Текст для поиска по описанию
     * @param pageable    Параметры пагинации
     * @return Список вещей
     */
    List<Item> getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description,
                                                                          Pageable pageable);

    /**
     * Получение всех вещей по идентификатору запроса вещи
     *
     * @param requestId Идентификатор запроса вещи
     * @return Список вещей
     */
    List<Item> getByRequestId(Long requestId);
}

package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Получение списка запросов вещей по идентификатору пользователя
     *
     * @param userId Идентификатор пользователя
     * @return Список запросов вещей
     */
    List<ItemRequest> findByUserId(Long userId);

    /**
     * Постраничный поиск списка запросов, созданных другими пользователями
     *
     * @param userId   Идентификатор текущего пользователя
     * @param pageable Параметры пагинации
     * @return Список запросов
     */
    List<ItemRequest> findByUserIdNot(Long userId, Pageable pageable);

}

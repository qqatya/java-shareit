package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Поиск комментариев по идентификатору вещи
     *
     * @param itemId Идентификатор вещи
     * @return Список комментариев
     */
    List<Comment> findByItemId(Long itemId);
}

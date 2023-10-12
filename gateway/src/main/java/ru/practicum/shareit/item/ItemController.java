package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.validation.ItemCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Header.SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    /**
     * Создание вещи
     *
     * @param dto     Объект, содержащий данные для создания
     * @param ownerId Идентификатор владельца
     * @return Созданная вещь
     */
    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SHARER_USER_ID) Long ownerId,
                                             @Validated(ItemCreate.class) @RequestBody ItemDto dto) {
        log.info("Creating item {}, ownerId = {}", dto, ownerId);
        return itemClient.createItem(dto, ownerId);
    }

    /**
     * Обновление вещи
     *
     * @param id      Идентификатор вещи
     * @param dto     Объект, содержащий данные для обновления
     * @param ownerId Идентификатор владельца
     * @return Обновленная вещь
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SHARER_USER_ID) Long ownerId,
                                             @PathVariable Long id,
                                             @RequestBody ItemDto dto) {
        log.info("Updating itemId = {}, ownerId = {}", id, ownerId);
        return itemClient.updateItem(ownerId, id, dto);
    }

    /**
     * Получение вещи по идентификатору
     *
     * @param id      Идентификатор вещи
     * @param ownerId Идентификатор владельца
     * @return Вещь
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id,
                                              @RequestHeader(SHARER_USER_ID) Long ownerId) {
        log.info("Get itemId = {}, ownerId = {}", id, ownerId);
        return itemClient.getItemById(id, ownerId);
    }

    /**
     * Постраничное получение всех вещей
     *
     * @param ownerId Идентификатор владельца
     * @param size    Количество элементов для отображения
     * @param from    Индекс первого элемента
     * @return Список вещей
     */
    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(SHARER_USER_ID) Long ownerId,
                                           @PositiveOrZero @RequestParam(required = false,
                                                   defaultValue = "0") Integer from,
                                           @Positive @RequestParam(required = false,
                                                   defaultValue = "10") Integer size) {
        log.info("Get items by ownerId = {}, from = {}, size = {}", ownerId, from, size);
        return itemClient.getItems(ownerId, size, from);
    }

    /**
     * Постраничный поиск вещей
     *
     * @param text Текст для поиска по названию/описанию
     * @param size Количество элементов для отображения
     * @param from Индекс первого элемента
     * @return Список найденных вещей
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(required = false,
                                                      defaultValue = "0") Integer from,
                                              @Positive @RequestParam(required = false,
                                                      defaultValue = "10") Integer size) {
        log.info("Search items by text = {}, from = {}, size = {}", text, from, size);
        return itemClient.searchItems(text, size, from);
    }

    /**
     * Создание комментария
     *
     * @param dto      Объект, содержащий текст комментария
     * @param itemId   Идентификатор вещи
     * @param authorId Идентификатор автора
     * @return Объект, содержащий созданный комментарий
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto dto,
                                                @PathVariable Long itemId,
                                                @RequestHeader(SHARER_USER_ID) Long authorId) {
        log.info("Create comment {}, itemId = {}, authorId = {}", dto, itemId, authorId);
        return itemClient.createComment(dto, itemId, authorId);
    }
}

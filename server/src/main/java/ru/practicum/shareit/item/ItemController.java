package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.util.Header.SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * Создание вещи
     *
     * @param dto     Объект, содержащий данные для создания
     * @param ownerId Идентификатор владельца
     * @return Созданная вещь
     */
    @PostMapping
    public ItemDto createItem(@RequestHeader(SHARER_USER_ID) Long ownerId, @RequestBody ItemDto dto) {
        return itemService.createItem(dto, ownerId);
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
    public ItemDto updateItem(@RequestHeader(SHARER_USER_ID) Long ownerId, @PathVariable Long id,
                              @RequestBody ItemDto dto) {
        return itemService.updateItem(ownerId, id, dto);
    }

    /**
     * Получение вещи по идентификатору
     *
     * @param id      Идентификатор вещи
     * @param ownerId Идентификатор владельца
     * @return Вещь
     */
    @GetMapping("/{id}")
    public ItemBookingDto getItemById(@PathVariable Long id, @RequestHeader(SHARER_USER_ID) Long ownerId) {
        return itemService.getItemById(id, ownerId);
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
    public List<ItemBookingDto> getItems(@RequestHeader(SHARER_USER_ID) Long ownerId, @RequestParam Integer size,
                                         @RequestParam Integer from) {
        return itemService.getItems(ownerId, size, from);
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
    public List<ItemDto> searchItems(@RequestParam String text, @RequestParam Integer size,
                                     @RequestParam Integer from) {
        return itemService.searchItems(text, size, from);
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
    public CommentDto createComment(@RequestBody CommentDto dto, @PathVariable Long itemId,
                                    @RequestHeader(SHARER_USER_ID) Long authorId) {
        return itemService.createComment(dto, itemId, authorId);
    }

}

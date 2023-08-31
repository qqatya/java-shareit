package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.validation.ItemCreate;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * Создание вещи
     *
     * @param dto Объект, содержащий данные для создания
     * @return Созданная вещь
     */
    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @Validated(ItemCreate.class) @RequestBody ItemDto dto) {
        return itemService.createItem(dto, ownerId);
    }

    /**
     * Обновление вещи
     *
     * @param id  Идентификатор вещи
     * @param dto Объект, содержащий данные для обновления
     * @return Обновленная вещь
     */
    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long id,
                              @RequestBody ItemDto dto) {
        return itemService.updateItem(ownerId, id, dto);
    }

    /**
     * Получение вещи по идентификатору
     *
     * @param id Идентификатор вещи
     * @return Вещь
     */
    @GetMapping("/{id}")
    public ItemBookingDto getItemById(@PathVariable Long id,
                                      @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemById(id, ownerId);
    }

    /**
     * Получение всех вещей
     *
     * @return Список вещей
     */
    @GetMapping
    public List<ItemBookingDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllItems(ownerId);
    }

    /**
     * Поиск вещей
     *
     * @param text Текст для поиска по названию/описанию
     * @return Список найденных вещей
     */
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}

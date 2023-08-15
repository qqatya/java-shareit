package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

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
                              @RequestBody ItemDto dto) {
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
    public ItemDto getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    /**
     * Получение всех вещей
     *
     * @return Список вещей
     */
    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllItems(ownerId);
    }

}

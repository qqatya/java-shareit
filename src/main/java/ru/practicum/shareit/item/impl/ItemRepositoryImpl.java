package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.type.ExceptionType.ITEM_NOT_FOUND;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    /**
     * Ключ - id вещи, значение - хранимый объект
     **/
    private final Map<Long, Item> items = new HashMap<>();

    private Long itemIdCounter = 0L;

    @Override
    public Item save(Item item) {
        item.setId(++itemIdCounter);
        items.put(item.getId(), item);
        Long itemId = item.getId();

        return getById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getValue() + itemId));
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        Long itemId = item.getId();

        return getById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getValue() + itemId));
    }

    @Override
    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwnerId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getByNameOrDescription(String text) {
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && i.getAvailable())
                .collect(Collectors.toList());
    }
}

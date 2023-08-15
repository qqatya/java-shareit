package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.type.ExceptionType.ITEM_NOT_FOUND;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final List<Item> items = new ArrayList<>();

    private Long itemIdCounter = 0L;

    @Override
    public Item save(Item item) {
        item.setId(++itemIdCounter);
        items.add(item);
        Long itemId = item.getId();

        return getById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getValue() + itemId));
    }

    @Override
    public Item update(Item item) {
        Optional<Item> toBeRemoved = getById(item.getId());

        toBeRemoved.ifPresent(items::remove);
        items.add(item);
        Long itemId = item.getId();

        return getById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getValue() + itemId));
    }

    @Override
    public Optional<Item> getById(Long id) {
        return items.stream()
                .filter(i -> Objects.equals(i.getId(), id))
                .findFirst();
    }

    @Override
    public List<Item> getByOwnerId(Long ownerId) {
        return items.stream()
                .filter(i -> Objects.equals(i.getOwnerId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getByNameOrDescription(String text) {
        return items.stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && i.getAvailable())
                .collect(Collectors.toList());
    }
}

package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidator;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.type.ExceptionType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemMapper itemMapper;

    private final ItemValidator itemValidator;

    @Override
    public ItemDto createItem(ItemDto dto, Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + ownerId);
        }
        itemValidator.validate(dto);
        Item item = itemMapper.mapToModel(dto, userRepository.findById(ownerId).get());

        log.info("creating new item");
        return itemMapper.mapToDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long id, ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getValue() + id));

        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new SecurityException(EDITING_DENIED.getValue() + id);
        }
        item = itemMapper.mapToModel(item, dto);

        log.info("updating itemId = {}", id);
        return itemMapper.mapToDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long id) {
        Optional<Item> result = itemRepository.findById(id);

        if (result.isPresent()) {
            log.info("getting item by id = {}", id);
            return itemMapper.mapToDto(result.get());
        }
        throw new NotFoundException(ITEM_NOT_FOUND.getValue() + id);
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + ownerId);
        }
        log.info("getting all items by ownerId = {}", ownerId);
        return itemRepository.getByOwnerId(ownerId).stream()
                .map(itemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("finding items by search criteria = {}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(itemMapper::mapToDto)
                .collect(Collectors.toList());
    }
}

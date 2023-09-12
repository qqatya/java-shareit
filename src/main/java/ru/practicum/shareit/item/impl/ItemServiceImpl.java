package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.type.ExceptionType.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemMapper itemMapper;

    private final BookingRepository bookingRepository;

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(ItemDto dto, Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + ownerId);
        }
        ItemRequest request = null;
        Long requestId = dto.getRequestId();

        if (requestId != null) {
            request = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException(ITEM_REQUEST_NOT_FOUND.getValue() + requestId));
        }
        User user = userRepository.findById(ownerId).get();
        Item item = itemMapper.mapToModel(dto, user, request);

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
    @Transactional(readOnly = true)
    public ItemBookingDto getItemById(Long id, Long userId) {
        Optional<Item> result = itemRepository.findById(id);

        if (result.isPresent()) {
            log.info("getting item by id = {}", id);
            Item item = result.get();
            if (item.getOwner().getId().equals(userId)) {
                log.info("item is owned by userId = {}", userId);
                Long itemId = item.getId();

                return itemMapper.mapToItemBookingDto(item, commentRepository.findByItemId(itemId),
                        bookingRepository.findLastByItemId(itemId).orElse(null),
                        bookingRepository.findNextByItemId(itemId).orElse(null));
            } else {
                log.info("item is not owned by userId = {}", userId);
                return itemMapper.mapToItemBookingDto(item, commentRepository.findByItemId(item.getId()));
            }
        }
        throw new NotFoundException(ITEM_NOT_FOUND.getValue() + id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingDto> getAllItems(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + ownerId);
        }
        log.info("getting all items by ownerId = {}", ownerId);
        return itemRepository.getByOwnerId(ownerId).stream()
                .map(i -> itemMapper.mapToItemBookingDto(i, commentRepository.findByItemId(i.getId()),
                        bookingRepository.findLastByItemId(i.getId()).orElse(null),
                        bookingRepository.findNextByItemId(i.getId()).orElse(null)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        log.info("finding items by search criteria = {}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(itemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto dto, Long itemId, Long authorId) {
        if (bookingRepository.wasItemBookedByUser(itemId, authorId) && itemRepository.existsById(itemId)) {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getValue() + itemId));
            User user = userRepository.findById(authorId)
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getValue() + authorId));

            log.info("creating new comment for itemId = {}, authorId = {}", itemId, authorId);
            Comment comment = commentMapper.mapToModel(dto, item, user);

            return commentMapper.mapToDto(commentRepository.save(comment));
        }
        throw new IllegalArgumentException(String.format(WASNT_BOOKED_BY_USER.getValue(), authorId));
    }
}

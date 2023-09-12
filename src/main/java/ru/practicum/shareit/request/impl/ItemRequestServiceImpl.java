package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.type.ExceptionType.ITEM_REQUEST_NOT_FOUND;
import static ru.practicum.shareit.exception.type.ExceptionType.USER_NOT_FOUND;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRequestMapper itemRequestMapper;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto dto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + userId);
        }
        ItemRequest itemRequest = itemRequestMapper.mapToModel(dto, userRepository.findById(userId).get());

        log.info("creating new item request");

        return itemRequestMapper.mapToDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + userId);
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findByUserId(userId);

        log.info("found {} item requests", itemRequests.size());
        return mapToDtos(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsOfOtherUsers(Long userId, Integer size, Integer from) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + userId);
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        return mapToDtos(itemRequestRepository.findByUserIdNot(userId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + userId);
        }
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ITEM_REQUEST_NOT_FOUND.getValue() + requestId));

        log.info("getting item request by id = {}", requestId);
        List<Item> items = itemRepository.getByRequestId(requestId);

        log.info("itemRequestId = {} has {} items", request, items.size());
        return itemRequestMapper.mapToDto(request, items);
    }

    private List<ItemRequestDto> mapToDtos(List<ItemRequest> requests) {
        return requests.stream().map(req -> {
                    Long reqId = req.getId();
                    List<Item> items = itemRepository.getByRequestId(reqId);

                    log.info("itemRequestId = {} has {} items", reqId, items.size());
                    return itemRequestMapper.mapToDto(req, items);
                })
                .collect(Collectors.toList());
    }
}

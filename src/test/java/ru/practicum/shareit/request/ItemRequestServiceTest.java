package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRequestMapper itemRequestMapper;

    @MockBean
    private final ItemRepository itemRepository;

    @InjectMocks
    private final ItemRequestService itemRequestService;

    private final User user = User.builder()
            .id(1L)
            .name("Ivan")
            .email("ivan@test.com")
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("Some description")
            .build();

    private final ItemRequest itemRequest = ItemRequest.builder()
            .user(user)
            .description(requestDto.getDescription())
            .createDttm(LocalDateTime.now())
            .build();

    @Test
    public void createItemRequestSavesRequest() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestMapper.mapToModel(requestDto, user)).thenReturn(itemRequest);

        itemRequestService.createItemRequest(1L, requestDto);

        verify(itemRequestRepository, times(1)).save(itemRequest);
    }

    @Test
    public void createItemRequestThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(1L, requestDto));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void getItemRequestByUserIdReturnsRequests() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        itemRequestService.getItemRequestsByUserId(1L);

        verify(itemRequestRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void getItemRequestByUserIdThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsByUserId(1L));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void getItemRequestsOfOtherUsersReturnsRequests() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        itemRequestService.getItemRequestsOfOtherUsers(1L, 2, 0);

        verify(itemRequestRepository, times(1))
                .findByUserIdNot(1L, PageRequest.of(0, 2));
    }

    @Test
    public void getItemRequestsOfOtherUsersThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsOfOtherUsers(1L, 2, 0));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void getItemRequestByIdReturnsRequest() {
        ItemRequest expected = itemRequest;
        expected.setId(1L);
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(expected));

        itemRequestService.getItemRequestById(1L, 1L);

        verify(itemRepository, times(1)).getByRequestId(expected.getId());
        verify(itemRequestMapper, times(1)).mapToDto(expected, List.of());
    }

    @Test
    public void getItemRequestByIdThrowsNotFoundExceptionWhenRequestNotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(1L, 1L));
        assertEquals("Не найден запрос вещи с id = 1", e.getMessage());
    }

    @Test
    public void getItemRequestByIdThrowsNotFoundExceptionWhenUserNotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(1L, 1L));
        assertEquals("Не найден пользователь с id = 1", e.getMessage());
    }

    @Test
    public void mapToDtosCreatesDtos() {
        Mockito.when(itemRepository.getByRequestId(anyLong())).thenReturn(List.of());
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        ItemRequest withId = itemRequest;
        withId.setId(1L);
        List<ItemRequest> requests = List.of(withId, withId);
        Mockito.when(itemRequestRepository.findByUserId(anyLong())).thenReturn(requests);

        itemRequestService.getItemRequestsByUserId(1L);

        verify(itemRepository, times(2)).getByRequestId(1L);
    }
}

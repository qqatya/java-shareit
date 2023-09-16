package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository itemRequestRepository;

    private User user;

    private Item item1;

    private Item item2;

    private ItemRequest request;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@test.com")
                .build());

        item1 = itemRepository.save(Item.builder()
                .owner(user)
                .name("Camera")
                .description("Analog camera")
                .available(true)
                .build());

        request = itemRequestRepository.save(ItemRequest.builder()
                .user(user)
                .description("Some description")
                .createDttm(LocalDateTime.now().minusDays(1))
                .build());

        item2 = itemRepository.save(Item.builder()
                .owner(user)
                .name("Sweater")
                .description("Red sweater")
                .request(request)
                .available(true)
                .build());
    }

    @AfterEach
    public void tearDown() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getByOwnerId() {
        List<Item> expected = List.of(item1, item2);

        List<Item> actual = itemRepository.getByOwnerId(user.getId(), PageRequest.of(0, 2));

        assertEquals(expected, actual);
    }

    @Test
    public void getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue_whenCamera_thenReturnItemId1() {
        List<Item> expected = List.of(item1);

        List<Item> actual = itemRepository
                .getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("Camera",
                        "Camera", PageRequest.of(0, 2));

        assertEquals(expected, actual);
    }

    @Test
    public void getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue_whenRed_thenReturnItemId2() {
        List<Item> expected = List.of(item2);

        List<Item> actual = itemRepository
                .getByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("Red",
                        "Red", PageRequest.of(0, 2));

        assertEquals(expected, actual);
    }

    @Test
    public void getByRequestId() {
        List<Item> expected = List.of(item2);

        List<Item> actual = itemRepository.getByRequestId(request.getId());

        assertEquals(expected, actual);
    }

}

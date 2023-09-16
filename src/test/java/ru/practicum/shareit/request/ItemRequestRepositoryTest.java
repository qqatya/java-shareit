package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    private User user1;

    private ItemRequest request1;

    private ItemRequest request2;

    @BeforeEach
    public void setUp() {
        user1 = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@test.com")
                .build());

        User user2 = userRepository.save(User.builder()
                .name("Arkadiy")
                .email("arkadiy@test.com")
                .build());

        request1 = itemRequestRepository.save(ItemRequest.builder()
                .user(user1)
                .description("Some description")
                .createDttm(LocalDateTime.now().minusDays(1))
                .build());

        request2 = itemRequestRepository.save(ItemRequest.builder()
                .user(user2)
                .description("One more description")
                .createDttm(LocalDateTime.now().minusDays(2))
                .build());
    }

    @AfterEach
    public void tearDown() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findByUserId() {
        List<ItemRequest> expected = List.of(request1);

        List<ItemRequest> actual = itemRequestRepository.findByUserId(user1.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findByUserIdNot() {
        List<ItemRequest> expected = List.of(request2);

        List<ItemRequest> actual = itemRequestRepository
                .findByUserIdNot(user1.getId(), PageRequest.of(0, 1));

        assertEquals(expected, actual);
    }

}

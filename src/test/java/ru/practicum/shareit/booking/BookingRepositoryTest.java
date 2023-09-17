package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.model.type.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.type.BookingStatus.WAITING;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private User user1;

    private User user2;

    private Item item;

    private Booking booking1;

    private Booking booking3;

    @BeforeEach
    public void setUp() {
        user1 = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@test.com")
                .build());
        user2 = userRepository.save(User.builder()
                .name("Arkadiy")
                .email("arkadiy@test.com")
                .build());
        item = itemRepository.save(Item.builder()
                .owner(user1)
                .name("Camera")
                .description("Analog camera")
                .available(true)
                .build());
        booking1 = bookingRepository.save(Booking.builder()
                .item(item)
                .initiator(user2)
                .startDttm(LocalDateTime.now().plusDays(2))
                .endDttm(LocalDateTime.now().plusDays(3))
                .status(APPROVED)
                .build());
        bookingRepository.save(Booking.builder()
                .item(item)
                .initiator(user2)
                .startDttm(LocalDateTime.now().plusDays(6))
                .endDttm(LocalDateTime.now().plusDays(7))
                .status(WAITING)
                .build());
        booking3 = bookingRepository.save(Booking.builder()
                .item(item)
                .initiator(user2)
                .startDttm(LocalDateTime.now().minusDays(2))
                .endDttm(LocalDateTime.now().minusDays(1))
                .status(APPROVED)
                .build());
    }

    @AfterEach
    public void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findIntersectionsReturnsIntersections() {
        Long itemId = item.getId();
        LocalDateTime expectedStart = booking1.getStartDttm();
        LocalDateTime expectedEnd = booking1.getEndDttm();
        List<Booking> intersections = bookingRepository.findIntersections(itemId, expectedStart, expectedEnd);

        assertEquals(1, intersections.size());
        Booking actual = intersections.get(0);
        assertEquals(actual.getItem().getId(), itemId);
        assertEquals(booking1.getStatus(), actual.getStatus());
        assertEquals(expectedStart, actual.getStartDttm());
        assertEquals(expectedEnd, actual.getEndDttm());
    }

    @Test
    public void findLastByItemIdReturnsLastBooking() {
        Long itemId = item.getId();
        Optional<Booking> actualOpt = bookingRepository.findLastByItemId(itemId);

        assertTrue(actualOpt.isPresent());
        Booking actual = actualOpt.get();
        assertEquals(actual.getItem().getId(), itemId);
        assertEquals(actual.getId(), booking3.getId());
        assertTrue(actual.getStartDttm().isBefore(LocalDateTime.now()));
    }

    @Test
    public void findNextByItemIdReturnsNextBooking() {
        Long itemId = item.getId();
        Optional<Booking> actualOpt = bookingRepository.findNextByItemId(itemId);

        assertTrue(actualOpt.isPresent());
        Booking actual = actualOpt.get();
        assertEquals(actual.getItem().getId(), itemId);
        assertEquals(actual.getId(), booking1.getId());
        assertTrue(actual.getStartDttm().isAfter(LocalDateTime.now()));
    }

    @Test
    public void findByIdAndItemOwnerIdOrInitiatorIdReturnsBooking() {
        Optional<Booking> actualOpt = bookingRepository
                .findByIdAndItemOwnerIdOrInitiatorId(booking1.getId(), user2.getId());

        assertTrue(actualOpt.isPresent());
        Booking actual = actualOpt.get();
        assertEquals(actual.getItem().getId(), item.getId());
        assertEquals(actual.getId(), booking1.getId());
        assertTrue(actual.getStartDttm().isAfter(LocalDateTime.now()));
    }

    @Test
    public void wasItemBookedByUserReturnsTrueWhenWasBooked() {
        Boolean result = bookingRepository.wasItemBookedByUser(item.getId(), user2.getId());

        assertTrue(result);
    }

    @Test
    public void wasItemBookedByUserReturnsFalseWhenWasntBooked() {
        Boolean result = bookingRepository.wasItemBookedByUser(item.getId(), user1.getId());

        assertFalse(result);
    }
}

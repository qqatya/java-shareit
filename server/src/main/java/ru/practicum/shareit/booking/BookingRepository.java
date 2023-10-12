package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    /**
     * Поиск бронирования по идентификатору и индентификатору владельца
     *
     * @param bookingId Идентификатор бронирования
     * @param ownerId   Идентификатор владельца
     * @return Бронирование
     */
    Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long ownerId);

    /**
     * Поиск пересечений по промежуткам бронирования
     *
     * @param itemId    Идентификатор вещи
     * @param startDttm Начало периода бронирования
     * @param endDttm   Окончание периода бронирования
     * @return Список бронирований, совпадающих по промежуткам
     */
    @Query("SELECT b FROM Booking b "
            + "WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.startDttm <= ?3 AND b.endDttm >= ?2")
    List<Booking> findIntersections(Long itemId, LocalDateTime startDttm, LocalDateTime endDttm);

    /**
     * Поиск последнего бронирования по идентификатору вещи
     *
     * @param itemId Идентификатор вещи
     * @return Список из бронирований по возрастанию даты начала
     */
    @Query(value = "SELECT * FROM bookings WHERE item_id = ?1 "
            + "AND status = 'APPROVED' and start_dttm < now() ORDER BY start_dttm DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> findLastByItemId(Long itemId);

    /**
     * Поиск ближайшего бронирования по идентификатору вещи
     *
     * @param itemId Идентификатор вещи
     * @return Список из бронирований по возрастанию даты начала
     */
    @Query(value = "SELECT * FROM bookings WHERE item_id = ?1 "
            + "AND status = 'APPROVED' and start_dttm > now() ORDER BY start_dttm LIMIT 1", nativeQuery = true)
    Optional<Booking> findNextByItemId(Long itemId);

    /**
     * Поиск бронирования по идентификатору и индентификатору владельца/инициатора брони
     *
     * @param bookingId Идентификатор бронирования
     * @param userId    Идентификатор владельца/инициатора
     * @return Бронирование
     */
    @Query("SELECT b FROM Booking b WHERE b.id = ?1 AND (b.item.owner.id = ?2 OR b.initiator.id = ?2)")
    Optional<Booking> findByIdAndItemOwnerIdOrInitiatorId(Long bookingId, Long userId);

    /**
     * Поиск бронирований по идентификатору инициатора бронирования
     *
     * @param initiatorId Идентификатор инициатора
     * @param pageable    Параметры пагинации
     * @return Список бронирований
     */
    List<Booking> findByInitiatorIdOrderByStartDttmDesc(Long initiatorId, Pageable pageable);

    /**
     * Получение списка бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param ownerId  Идентификатор владельца
     * @param pageable Параметры пагинации
     * @return Список бронирований
     */
    List<Booking> findByItemOwnerIdOrderByStartDttmDesc(Long ownerId, Pageable pageable);

    /**
     * Проверка, была ли вещь забронирована пользователем
     *
     * @param itemId Идентификатор вещи
     * @param userId Идентификатор пользователя
     * @return true, если была бронь
     */
    @Query("SELECT count(b) > 0 FROM Booking b "
            + "WHERE b.item.id = ?1 AND b.initiator.id = ?2 AND b.status != 'REJECTED' AND b.endDttm < now()")
    Boolean wasItemBookedByUser(Long itemId, Long userId);

}

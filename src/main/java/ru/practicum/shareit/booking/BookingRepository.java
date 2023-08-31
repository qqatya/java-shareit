package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

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
     * Поиск последнего и ближайшего слудующего бронирования по идентификатору вещи
     *
     * @param itemId Идентификатор вещи
     * @return Список из бронирований по возрастанию даты начала
     */
    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 ORDER BY b.startDttm")
    List<Booking> findByItemId(Long itemId);

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
     * @return Список бронирований
     */
    List<Booking> findByInitiatorIdOrderByStartDttmDesc(Long initiatorId);

    /**
     * Поиск текущих бронирований по идентификатору инициатора бронирования
     *
     * @param currentDate Текущая дата (для сравнения границ периода бронирования)
     * @param userId      Идентификатор пользователя
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.startDttm < ?1 AND b.endDttm > ?1 "
            + "AND b.initiator.id = ?2 ORDER BY b.startDttm DESC")
    List<Booking> findCurrent(LocalDateTime currentDate, Long userId);

    /**
     * Поиск завершенных бронирований по идентификатору инициатора бронирования
     *
     * @param currentDate Текущая дата (для сравнения границ периода бронирования)
     * @param userId      Идентификатор пользователя
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.status = 'APPROVED' AND b.endDttm < ?1 AND b.initiator.id = ?2 "
            + "ORDER BY b.startDttm DESC")
    List<Booking> findPast(LocalDateTime currentDate, Long userId);

    /**
     * Поиск будущих бронирований по идентификатору инициатора бронирования и статусу бронирования
     *
     * @param currentDate Текущая дата (для сравнения границ периода бронирования)
     * @param userId      Идентификатор пользователя
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.status != 'REJECTED' AND b.startDttm > ?1 AND b.initiator.id = ?2 "
            + "ORDER BY b.startDttm DESC")
    List<Booking> findFuture(LocalDateTime currentDate, Long userId);

    /**
     * Поиск бронирований в ожидании по идентификатору инициатора бронирования и статусу бронирования
     *
     * @param userId Идентификатор пользователя
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.status = 'WAITING' AND b.initiator.id = ?1 ORDER BY b.startDttm DESC")
    List<Booking> findWaiting(Long userId);

    /**
     * Поиск отклоненных бронирований по идентификатору инициатора бронирования и статусу бронирования
     *
     * @param userId Идентификатор пользователя
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.status = 'REJECTED' AND b.initiator.id = ?1 ORDER BY b.startDttm DESC")
    List<Booking> findRejected(Long userId);


    /**
     * Получение списка бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param ownerId Идентификатор владельца
     * @return Список бронирований
     */
    List<Booking> findByItemOwnerIdOrderByStartDttmDesc(Long ownerId);

    /**
     * Получение списка текущих бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param currentDate Текущая дата (для сравнения границ периода бронирования)
     * @param ownerId     Идентификатор владельца
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.startDttm < ?1 AND b.endDttm > ?1 "
            + "AND b.item.owner.id = ?2 ORDER BY b.startDttm DESC")
    List<Booking> findCurrentByOwnerId(LocalDateTime currentDate, Long ownerId);

    /**
     * Получение списка завершенных бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param currentDate Текущая дата (для сравнения границ периода бронирования)
     * @param ownerId     Идентификатор владельца
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.status = 'APPROVED' AND b.endDttm < ?1 AND b.item.owner.id = ?2 "
            + "ORDER BY b.startDttm DESC")
    List<Booking> findPastByOwnerId(LocalDateTime currentDate, Long ownerId);

    /**
     * Получение списка будущих бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param currentDate Текущая дата (для сравнения границ периода бронирования)
     * @param ownerId     Идентификатор владельца
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.status != 'REJECTED' AND b.startDttm > ?1 AND b.item.owner.id = ?2 "
            + "ORDER BY b.startDttm DESC")
    List<Booking> findFutureByOwnerId(LocalDateTime currentDate, Long ownerId);

    /**
     * Получение списка бронирований в ожидании для всех вещей по идентификатору владельца вещи
     *
     * @param ownerId Идентификатор владельца
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.status = 'WAITING' AND b.item.owner.id = ?1 ORDER BY b.startDttm DESC")
    List<Booking> findWaitingByOwnerId(Long ownerId);

    /**
     * Получение списка отклоненных бронирований для всех вещей по идентификатору владельца вещи
     *
     * @param ownerId Идентификатор владельца
     * @return Список бронирований
     */
    @Query("SELECT b from Booking b "
            + "WHERE b.status = 'REJECTED' AND b.item.owner.id = ?1 ORDER BY b.startDttm DESC")
    List<Booking> findRejectedByOwnerId(Long ownerId);
}

package ru.practicum.shareit.booking.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.type.BookingSearchType;
import ru.practicum.shareit.exception.BookingPeriodException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.practicum.shareit.booking.model.type.BookingSearchType.ALL;
import static ru.practicum.shareit.booking.model.type.BookingStatus.*;
import static ru.practicum.shareit.exception.type.ExceptionType.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final String START_DTTM = "startDttm";

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(BookingDto dto, Long initiatorId) {
        if (dto.getStart().isBefore(LocalDateTime.now()) || dto.getEnd().isBefore(dto.getStart())
                || dto.getEnd().isEqual(dto.getStart())) {
            throw new BookingPeriodException();
        }
        Long itemId = dto.getItemId();
        User initiator = userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getValue() + initiatorId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getValue() + itemId));

        checkArgument(item.getAvailable(), "item is unavailable for booking");
        if (item.getOwner().getId().equals(initiatorId)) {
            throw new NotFoundException(BOOKING_BY_OWNER.getValue());
        }
        if (!bookingRepository.findIntersections(itemId, dto.getStart(), dto.getEnd()).isEmpty()) {
            throw new NotFoundException(INVALID_BOOKING_PERIOD.getValue());
        }
        Booking booking = bookingMapper.mapToModel(dto, initiator, item, WAITING);

        log.info("creating new booking for itemId = {} by userId = {}", itemId, initiatorId);
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto changeStatus(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND.getValue() + bookingId));

        if (APPROVED.equals(booking.getStatus())) {
            throw new UnsupportedOperationException(String.format(ALREADY_APPROVED.getValue(), bookingId));
        }
        booking.setStatus(approved ? APPROVED : REJECTED);
        log.info("approval status of bookingId = {} is {}", bookingId, booking.getStatus().toString());
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndItemOwnerIdOrInitiatorId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND.getValue() + bookingId));

        log.info("returning bookingId = {}", bookingId);
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUserId(BookingSearchType state, Long userId, Integer size, Integer from) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + userId);
        }
        List<Booking> bookings;
        int page = from != 0 ? from / size : from;
        Pageable pageable = PageRequest.of(page, size, Sort.by(START_DTTM).descending());

        log.info("searching for current userId = {} bookings by status = {}", userId, state.toString());
        if (ALL.equals(state)) {
            bookings = bookingRepository.findByInitiatorIdOrderByStartDttmDesc(userId, pageable);
        } else {
            bookings = find(userId, state, pageable, false).getContent();
        }
        log.info("found {} bookings", bookings.size());
        return bookings.stream()
                .map(bookingMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByItemOwner(BookingSearchType state, Long ownerId, Integer size, Integer from) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(USER_NOT_FOUND.getValue() + ownerId);
        }
        List<Booking> bookings;
        int page = from != 0 ? from / size : from;
        Pageable pageable = PageRequest.of(page, size, Sort.by(START_DTTM).descending());

        log.info("searching for owner userId = {} bookings by status = {}", ownerId, state.toString());
        if (ALL.equals(state)) {
            bookings = bookingRepository.findByItemOwnerIdOrderByStartDttmDesc(ownerId, pageable);
        } else {
            bookings = find(ownerId, state, pageable, true).getContent();
        }
        log.info("found {} bookings", bookings.size());
        return bookings.stream()
                .map(bookingMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private Page<Booking> find(Long userId, BookingSearchType state, Pageable pageable, boolean isOwner) {
        BooleanExpression startDttmLessThanNow = QBooking.booking.startDttm.before(LocalDateTime.now());
        BooleanExpression endDttmMoreThanNow = QBooking.booking.endDttm.after(LocalDateTime.now());
        BooleanExpression endDttmLessThanNow = QBooking.booking.endDttm.before(LocalDateTime.now());
        BooleanExpression startDttmMoreThanNow = QBooking.booking.startDttm.after(LocalDateTime.now());
        BooleanExpression isApproved = QBooking.booking.status.eq(APPROVED);
        BooleanExpression isNotRejected = QBooking.booking.status.ne(REJECTED);
        BooleanExpression isRejected = QBooking.booking.status.eq(REJECTED);
        BooleanExpression isWaiting = QBooking.booking.status.eq(WAITING);
        BooleanExpression byUserId = QBooking.booking.item.owner.id.eq(userId);
        BooleanExpression byInitiatorId = QBooking.booking.initiator.id.eq(userId);

        switch (state) {
            case CURRENT:
                return isOwner
                        ? bookingRepository.findAll(startDttmLessThanNow.and(endDttmMoreThanNow).and(byUserId), pageable)
                        : bookingRepository.findAll(
                        startDttmLessThanNow.and(endDttmMoreThanNow), pageable);
            case PAST:
                return isOwner
                        ? bookingRepository.findAll(isApproved.and(endDttmLessThanNow).and(byUserId), pageable)
                        : bookingRepository.findAll(isApproved.and(endDttmLessThanNow), pageable);
            case FUTURE:
                return isOwner
                        ? bookingRepository.findAll(isNotRejected.and(startDttmMoreThanNow).and(byUserId), pageable)
                        : bookingRepository.findAll(isNotRejected.and(startDttmMoreThanNow), pageable);
            case WAITING:
                return isOwner ? bookingRepository.findAll(isWaiting.and(byUserId), pageable)
                        : bookingRepository.findAll(isWaiting, pageable);
            case REJECTED:
                return isOwner
                        ? bookingRepository.findAll(isRejected.and(byUserId), pageable)
                        : bookingRepository.findAll(isRejected.and(byInitiatorId), pageable);
            default:
                throw new NotFoundException(String.format(INVALID_BOOKING_STATE.getValue(), state));
        }
    }


}

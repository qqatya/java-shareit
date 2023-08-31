package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final BookingInfoMapper bookingMapper;

    public Item mapToModel(Item item, ItemDto dto) {
        return Item.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(dto.getName() != null ? dto.getName() : item.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : item.getDescription())
                .available(dto.getAvailable() != null ? dto.getAvailable() : item.getAvailable())
                .build();
    }

    public Item mapToModel(ItemDto dto, User owner) {
        return Item.builder()
                .owner(owner)
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemBookingDto mapToItemBookingDto(Item item, List<Booking> bookings) {
        int lastBookingIdx = 0;
        int nextBookingIdx = 1;
        Booking nextBooking = bookings.size() > nextBookingIdx ? bookings.get(nextBookingIdx) : null;
        Booking lastBooking = !bookings.isEmpty() ? bookings.get(lastBookingIdx) : null;

        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(bookingMapper.mapToDto(lastBooking))
                .nextBooking(bookingMapper.mapToDto(nextBooking))
                .build();
    }

    public ItemBookingDto mapToItemBookingDto(Item item) {
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

}

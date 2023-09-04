package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.model.type.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "start_dttm")
    private LocalDateTime startDttm;

    @Column(name = "end_dttm")
    private LocalDateTime endDttm;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}

package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BookingPeriodException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.type.BookingSearchType.ALL;
import static ru.practicum.shareit.booking.model.type.BookingStatus.APPROVED;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final BookingDto dto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(2))
            .end(LocalDateTime.now().plusDays(3))
            .build();

    @Test
    @SneakyThrows
    public void create() {
        BookingDto expected = BookingDto.builder()
                .id(1L)
                .itemId(dto.getItemId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .build();
        Mockito.when(bookingService.create(any(BookingDto.class), anyLong()))
                .thenReturn(expected);

        String response = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, BookingDto.class));
    }

    @Test
    @SneakyThrows
    public void create_whenIntersectsOtherBookingPeriods_thenBadRequest() {
        Mockito.when(bookingService.create(any(BookingDto.class), anyLong()))
                .thenThrow(BookingPeriodException.class);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void create_whenUserNotFound_thenNotFound() {
        Mockito.when(bookingService.create(any(BookingDto.class), anyLong()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    @SneakyThrows
    public void changeStatus() {
        BookingDto expected = BookingDto.builder()
                .id(1L)
                .itemId(dto.getItemId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(APPROVED)
                .build();
        Mockito.when(bookingService.changeStatus(1L, true, 1L))
                .thenReturn(expected);

        String response = mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, BookingDto.class));
    }

    @Test
    @SneakyThrows
    public void changeStatus_whenAlreadyWasApproved_thenBadRequest() {
        Mockito.when(bookingService.changeStatus(1L, true, 1L))
                .thenThrow(UnsupportedOperationException.class);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void getBookingById() {
        BookingDto expected = BookingDto.builder()
                .id(1L)
                .itemId(dto.getItemId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .build();
        Mockito.when(bookingService.getBookingById(1L, 1L))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, BookingDto.class));
    }

    @Test
    @SneakyThrows
    public void getBookingsByUserId() {
        List<BookingDto> expected = List.of(dto, dto, dto);
        Mockito.when(bookingService.getBookingsByUserId(ALL, 1L, 3, 0))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/bookings?size=3&from=0")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), response);
    }

    @Test
    @SneakyThrows
    public void getBookingsByUserId_whenIncorrectSize_thenInternalServerError() {
        mockMvc.perform(get("/bookings?size=0&from=0")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    public void getBookingsByUserId_whenIncorrectFrom_thenInternalServerError() {
        mockMvc.perform(get("/bookings?size=1&from=-1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    public void getBookingsByUserId_whenUnsupportedState_thenThrowsUnsupportedStateException() {
        List<BookingDto> expected = List.of(dto, dto, dto);
        Mockito.when(bookingService.getBookingsByUserId(ALL, 1L, 3, 0))
                .thenReturn(expected);

        mockMvc.perform(get("/bookings?state=NONE&size=3&from=0")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UnsupportedStateException))
                .andExpect(result -> assertEquals("Unknown state: NONE",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @SneakyThrows
    public void getBookingsByItemOwner() {
        List<BookingDto> expected = List.of(dto, dto, dto);
        Mockito.when(bookingService.getBookingsByItemOwner(ALL, 1L, 3, 0))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/bookings/owner?size=3&from=0")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), response);
    }

    @Test
    @SneakyThrows
    public void getBookingsByItemOwner_whenUnsupportedState_thenThrowsUnsupportedStateException() {
        List<BookingDto> expected = List.of(dto, dto, dto);
        Mockito.when(bookingService.getBookingsByItemOwner(ALL, 1L, 3, 0))
                .thenReturn(expected);

        mockMvc.perform(get("/bookings/owner?state=NONE&size=3&from=0")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UnsupportedStateException))
                .andExpect(result -> assertEquals("Unknown state: NONE",
                        result.getResolvedException().getMessage()));
    }

}

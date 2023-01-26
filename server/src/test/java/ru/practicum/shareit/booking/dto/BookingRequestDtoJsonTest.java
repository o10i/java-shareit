package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {
    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testBookingShortDto() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));

        JsonContent<BookingRequestDto> result = json.write(bookingRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
    }
}
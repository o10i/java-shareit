package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingShortDtoJsonTest {
    @Autowired
    private JacksonTester<BookingShortDto> json;

    @Test
    void testBookingShortDto() throws Exception {
        BookingShortDto bookingShortDto = new BookingShortDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));

        JsonContent<BookingShortDto> result = json.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
    }
}
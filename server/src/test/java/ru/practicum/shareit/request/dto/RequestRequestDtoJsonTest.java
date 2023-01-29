package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class RequestRequestDtoJsonTest {
    @Autowired
    private JacksonTester<RequestRequestDto> json;

    @Test
    void testRequestShortDto() throws Exception {
        RequestRequestDto requestRequestDto = new RequestRequestDto("testDescription");

        JsonContent<RequestRequestDto> result = json.write(requestRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");
    }
}

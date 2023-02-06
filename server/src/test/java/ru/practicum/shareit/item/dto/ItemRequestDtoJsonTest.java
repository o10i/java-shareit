package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testCommentDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "testName", "testDescription", true, null);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("testName");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isNull();
    }
}
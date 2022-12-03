package ru.practicum.shareit.request;

import lombok.Data;

@Data
public class ItemRequest {
    Long id;
    String description;
    Long requestor;
    String created;
}

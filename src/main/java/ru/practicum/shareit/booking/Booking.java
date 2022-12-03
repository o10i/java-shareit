package ru.practicum.shareit.booking;

import lombok.Data;

@Data
public class Booking {
    Long id;
    String start;
    String end;
    Long item;
    Long booker;
    Status status;
}

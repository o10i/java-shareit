package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class User {
    Long id;
    String name;
    @NotNull
    @Email
    String email;
}

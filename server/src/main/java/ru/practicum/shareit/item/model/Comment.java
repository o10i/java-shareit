package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String text;
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    Item item;
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    User author;
    @Column
    LocalDateTime created;
}

package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    ItemRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void search() {
        User owner = new User(1L, "userName", "email@email.ru");
        userRepository.save(owner);
        Item item = new Item(1L, "itemName", "itemDescription", true, owner.getId(), null, null, null, null);
        repository.save(item);

        List<Item> items = repository.search("item", PageRequest.of(0, 2)).getContent();

        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getName()).isEqualTo("itemName");
    }
}
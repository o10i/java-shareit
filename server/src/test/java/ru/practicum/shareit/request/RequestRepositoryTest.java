package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.RequestShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.practicum.shareit.request.RequestMapper.toRequest;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestRepositoryTest {
    @Autowired
    RequestRepository repository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findAllByRequestorOrderByCreatedDesc() {
        User requestor = new User(1L, "name", "email@email.ru");
        userRepository.save(requestor);

        RequestShortDto requestShortDto = new RequestShortDto("test");
        Request request = toRequest(requestShortDto, requestor);
        repository.save(request);

        List<Request> requests = repository.findAllByRequestorOrderByCreatedDesc(requestor);

        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.get(0)).isEqualTo(request);
    }
}
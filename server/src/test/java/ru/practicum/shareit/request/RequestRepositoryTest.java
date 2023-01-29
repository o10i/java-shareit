package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.practicum.shareit.request.dto.RequestMapper.toRequest;

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

        RequestRequestDto requestRequestDto = new RequestRequestDto("test");
        Request request = toRequest(requestRequestDto, requestor);
        repository.save(request);

        List<Request> requests = repository.findAllByRequestorOrderByCreatedDesc(requestor);

        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.get(0)).isEqualTo(request);
    }
}
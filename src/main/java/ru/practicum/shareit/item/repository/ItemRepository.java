package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerIdOrderById(Long userId, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    @Query("select i " +
            "from Item i " +
            "where i.available = true " +
            "and (lower(i.name) like lower(concat('%', ?1, '%')) " +
            "or lower(i.description) like lower(concat('%', ?1, '%')))")
    Page<Item> search(String text, Pageable pageable);
}

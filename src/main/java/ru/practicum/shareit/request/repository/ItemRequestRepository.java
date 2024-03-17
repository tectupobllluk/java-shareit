package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequester_Id(Long requester, Sort sort);

    @Query("select r from ItemRequest r " +
            "where r.requester.id != ?1")
    Page<ItemRequest> findOtherRequests(Long requester, Pageable page);
}

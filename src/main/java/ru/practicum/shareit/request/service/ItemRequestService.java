package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto saveItem(Long ownerId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getAllOwnerRequests(Long requester);

    List<ItemRequestResponseDto> getAllRequests(Long ownerId, Integer from, Integer size);

    ItemRequestResponseDto getRequestById(Long requester, Long requestId);
}

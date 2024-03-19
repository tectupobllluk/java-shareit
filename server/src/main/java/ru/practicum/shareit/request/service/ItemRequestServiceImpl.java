package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponseDto saveItem(Long ownerId, ItemRequestDto itemRequestDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id - " + ownerId + " not found"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(owner, itemRequestDto);
        return ItemRequestMapper.toItemRequestResponseDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestResponseDto> getAllOwnerRequests(Long requester) {
        User owner = userRepository.findById(requester)
                .orElseThrow(() -> new NotFoundException("User with id - " + requester + " not found"));
        Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");
        List<ItemRequest> response = itemRequestRepository.findByRequester_Id(requester, sort);
        return response.stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .peek(this::loadItems)
                .collect(Collectors.toList());
    }

    private void loadItems(ItemRequestResponseDto itemRequestResponseDto) {
        List<Item> items = itemRepository.findByRequestId(itemRequestResponseDto.getId());
        if (!items.isEmpty()) {
            List<ItemDto> itemDto = items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            itemRequestResponseDto.setItems(itemDto);
        } else {
            itemRequestResponseDto.setItems(Collections.emptyList());
        }
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long requester, Integer from, Integer size) {
        User owner = userRepository.findById(requester)
                .orElseThrow(() -> new NotFoundException("User with id - " + requester + " not found"));
        Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");
        Pageable page = PageRequest.of(from / size, size, sort);
        return itemRequestRepository.findOtherRequests(requester, page).stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .peek(this::loadItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long requester, Long requestId) {
        User owner = userRepository.findById(requester)
                .orElseThrow(() -> new NotFoundException("User with id - " + requester + " not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id - " + requestId + " not found"));
        ItemRequestResponseDto response = ItemRequestMapper.toItemRequestResponseDto(itemRequest);
        loadItems(response);
        return response;
    }
}

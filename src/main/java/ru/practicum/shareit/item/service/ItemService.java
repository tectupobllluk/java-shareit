package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, ItemDto itemDto, Long itemId);

    ItemDto getItem(Long itemId);

    List<ItemDto> getAllOwnerItems(Long ownerId);

    List<ItemDto> searchItem(String searchText);
}

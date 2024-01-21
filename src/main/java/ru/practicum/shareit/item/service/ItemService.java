package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto saveItem(Long ownerId, ItemDto itemDto);

    ItemResponseDto updateItem(Long ownerId, ItemDto itemDto, Long itemId);

    ItemResponseDto getItem(Long userId, Long itemId);

    List<ItemResponseDto> getAllOwnerItems(Long ownerId);

    List<ItemResponseDto> searchItem(Long userId, String searchText);

    CommentDto addComment(Long ownerId, Long itemId, CommentDto commentDto);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {
    void saveItem(@Valid Item item);

    Item updateItem(Long ownerId, ItemDto itemDto, Long itemId);

    Item getItem(Long itemId);

    List<Item> getAllOwnerItems(Long ownerId);

    List<Item> searchItem(String searchText);
}

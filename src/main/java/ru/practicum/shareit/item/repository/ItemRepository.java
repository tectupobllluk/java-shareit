package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    void saveItem(Item item);

    Item updateItem(Long ownerId, ItemDto itemDto, Long itemId);

    Item getItem(Long itemId);

    List<Item> getAllOwnerItems(Long ownerId);

    List<Item> searchItem(String searchText);
}

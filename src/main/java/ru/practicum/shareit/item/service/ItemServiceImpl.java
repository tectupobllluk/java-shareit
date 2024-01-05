package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public void saveItem(@Valid Item item) {
        itemRepository.saveItem(item);
    }

    @Override
    public Item updateItem(Long ownerId, ItemDto itemDto, Long itemId) {
        return itemRepository.updateItem(ownerId, itemDto, itemId);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<Item> getAllOwnerItems(Long ownerId) {
        return itemRepository.getAllOwnerItems(ownerId);
    }

    @Override
    public List<Item> searchItem(String searchText) {
        return itemRepository.searchItem(searchText);
    }
}

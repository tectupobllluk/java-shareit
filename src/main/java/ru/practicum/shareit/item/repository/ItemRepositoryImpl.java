package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final UserRepository userRepository;
    private static final HashMap<Long, Item> items = new HashMap<>();
    private static long generatedId = 0;

    private Long generateId() {
        return ++generatedId;
    }

    @Override
    public void saveItem(Item item) {
        userRepository.getUser(item.getOwner())
                .orElseThrow(() -> new NotFoundException("Owner with id " + item.getOwner() + " is not created!"));
        item.setId(generateId());
        items.put(item.getId(), item);
    }

    @Override
    public Item updateItem(Long ownerId, ItemDto itemDto, Long itemId) {
        if (!items.get(itemId).getOwner().equals(ownerId)) {
            throw new NotFoundException("Item owner and request owner not equals!");
        }
        userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner with id " + ownerId + " is not created!"));

        Item item = items.get(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    @Override
    public Item getItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item with id " + itemId + " not found!");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllOwnerItems(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String searchText) {
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(searchText.toLowerCase())))
                .collect(Collectors.toList());
    }
}

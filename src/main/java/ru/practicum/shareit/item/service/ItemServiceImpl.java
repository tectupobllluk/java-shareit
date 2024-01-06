package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto saveItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner with id " + ownerId + " is not created!"));
        Item item = ItemMapper.toItem(owner, itemDto);
        return ItemMapper.toItemDto(itemRepository.saveItem(item));
    }

    @Override
    public ItemDto updateItem(Long ownerId, ItemDto itemDto, Long itemId) {
        userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner with id " + ownerId + " is not created!"));
        if (!itemRepository.getItem(itemId).getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Item owner and request owner not equals!");
        }

        Item item = itemRepository.getItem(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public List<ItemDto> getAllOwnerItems(Long ownerId) {
        if (ownerId == null) {
            throw new BadRequestException("Owner can't be empty!");
        }
        return itemRepository.getAllOwnerItems(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String searchText) {
        if (searchText.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItem(searchText).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

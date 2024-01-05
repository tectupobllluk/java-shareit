package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item createItem(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                           @RequestBody ItemDto itemDto) {
        if (ownerId == null) {
            throw new BadRequestException("Owner can't be empty!");
        }
        log.info("Create item: {} - Started!", itemDto);
        Item item = ItemMapper.toItem(ownerId, itemDto);
        itemService.saveItem(item);
        log.info("Create item: {} - Finished!", item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody ItemDto itemDto,
                           @PathVariable Long itemId) {
        if (ownerId == null) {
            throw new BadRequestException("Owner can't be empty!");
        }
        return itemService.updateItem(ownerId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<Item> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        if (ownerId == null) {
            throw new BadRequestException("Owner can't be empty!");
        }
        return itemService.getAllOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemService.searchItem(text);
    }
}

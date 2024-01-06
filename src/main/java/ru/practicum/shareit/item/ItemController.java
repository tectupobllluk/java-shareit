package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                              @RequestBody @Validated(Create.class) ItemDto itemDto) {
        log.info("Create item: {} - Started!", itemDto);
        ItemDto item = itemService.saveItem(ownerId, itemDto);
        log.info("Create item: {} - Finished!", item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId, @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("Update item: {} - Started!", itemDto);
        ItemDto item = itemService.updateItem(ownerId, itemDto, itemId);
        log.info("Update item: {} - Finished!", item);
        return item;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Get item with id {} - Started!", itemId);
        ItemDto item = itemService.getItem(itemId);
        log.info("Get item: {} - Finished!", item);
        return item;
    }

    @GetMapping
    public List<ItemDto> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get all owner items with id {} - Started!", ownerId);
        List<ItemDto> ownerItems = itemService.getAllOwnerItems(ownerId);
        log.info("Get all owner items: {} - Finished!", ownerItems);
        return ownerItems;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("Search items with text {} - Started!", text);
        List<ItemDto> searchedItems = itemService.searchItem(text);
        log.info("Search items: {} - Finished!", searchedItems);
        return searchedItems;
    }
}

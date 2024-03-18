package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                      @RequestBody ItemDto itemDto) {
        log.info("Create item: {} - Started!", itemDto);
        ItemResponseDto item = itemService.saveItem(ownerId, itemDto);
        log.info("Create item: {} - Finished!", item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @RequestBody ItemDto itemDto,
                                      @PathVariable Long itemId) {
        log.info("Update item: {} - Started!", itemDto);
        ItemResponseDto item = itemService.updateItem(ownerId, itemDto, itemId);
        log.info("Update item: {} - Finished!", item);
        return item;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        log.info("Get item with id {} - Started!", itemId);
        ItemResponseDto item = itemService.getItem(userId, itemId);
        log.info("Get item: {} - Finished!", item);
        return item;
    }

    @GetMapping
    public List<ItemResponseDto> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all owner items with id {} - Started!", ownerId);
        List<ItemResponseDto> ownerItems = itemService.getAllOwnerItems(ownerId, from, size);
        log.info("Get all owner items: {} - Finished!", ownerItems);
        return ownerItems;
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam String text,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Search items with text {} - Started!", text);
        List<ItemResponseDto> searchedItems = itemService.searchItem(userId, text, from, size);
        log.info("Search items: {} - Finished!", searchedItems);
        return searchedItems;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Add comment: {} - Started!", commentDto);
        CommentDto responseCommentDto = itemService.addComment(ownerId, itemId, commentDto);
        log.info("Add comment: {} - Finished!", responseCommentDto);
        return responseCommentDto;
    }
}

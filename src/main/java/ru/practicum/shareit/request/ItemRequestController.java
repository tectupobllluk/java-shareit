package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Create item request: {} - Started!", itemRequestDto);
        ItemRequestResponseDto response = itemRequestService.saveItem(ownerId, itemRequestDto);
        log.info("Create item request: {} - Finished!", response);
        return response;
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllOwnerRequests(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId) {
        log.info("Get all owner item requests with id {} - Started!", ownerId);
        List<ItemRequestResponseDto> response = itemRequestService.getAllOwnerRequests(ownerId);
        log.info("Get all owner item requests: {} - Finished!", response);
        return response;
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all item requests from - {}; size - {} by user id - {} - Started!", from, size, ownerId);
        List<ItemRequestResponseDto> response = itemRequestService.getAllRequests(ownerId, from, size);
        log.info("Get all item requests: {} - Finished!", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequest(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                             @PathVariable Long requestId) {
        log.info("Get item request with id {} - Started!", requestId);
        ItemRequestResponseDto response = itemRequestService.getRequestById(ownerId, requestId);
        log.info("Get all owner item requests: {} - Finished!", response);
        return response;
    }
}

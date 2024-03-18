package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Create item request: {} - Started!", itemRequestDto);
        return requestClient.saveRequest(ownerId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerRequests(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId) {
        log.info("Get all owner item requests with id {} - Started!", ownerId);
        return requestClient.getAllOwnerRequests(ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all item requests from - {}; size - {} by user id - {} - Started!", from, size, ownerId);
        return requestClient.getAllRequests(ownerId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                             @PathVariable Long requestId) {
        log.info("Get item request with id {} - Started!", requestId);
        return requestClient.getRequestById(ownerId, requestId);
    }
}

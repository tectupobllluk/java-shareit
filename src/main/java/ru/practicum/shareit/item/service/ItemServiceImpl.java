package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemResponseDto saveItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner with id " + ownerId + " is not created!"));
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElse(null);
        }
        Item item = ItemMapper.toItem(owner, itemRequest, itemDto);
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto updateItem(Long ownerId, ItemDto itemDto, Long itemId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner with id " + ownerId + " is not created!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " is not created!"));
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("Item owner and request owner not equals!");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        ItemResponseDto itemDtoResponse = ItemMapper.toItemResponseDto(itemRepository.save(item));
        loadBookings(itemDtoResponse);
        loadComments(itemDtoResponse);
        return itemDtoResponse;
    }

    @Override
    public ItemResponseDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found!"));
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item);
        if (item.getOwner().getId().equals(userId)) {
            loadBookings(itemResponseDto);
        }
            loadComments(itemResponseDto);
        return itemResponseDto;
    }

    private void loadBookings(ItemResponseDto itemResponseDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        List<Booking> bookingList = bookingRepository.findByItem_Id(itemResponseDto.getId(), sort);
        List<Booking> lastBookingList = bookingList.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                        !booking.getStatus().equals(BookingStateEnum.REJECTED))
                .collect(Collectors.toList());
        if (!lastBookingList.isEmpty()) {
            Booking lastBooking = lastBookingList.get(lastBookingList.size() - 1);
            itemResponseDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }
        List<Booking> nextBookingList = bookingList.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) &&
                        !booking.getStatus().equals(BookingStateEnum.REJECTED))
                .collect(Collectors.toList());
        if (!nextBookingList.isEmpty()) {
            Booking nextBooking = nextBookingList.get(0);
            itemResponseDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }
    }

    @Override
    public List<ItemResponseDto> getAllOwnerItems(Long ownerId, Integer from, Integer size) {
        if (ownerId == null) {
            throw new BadRequestException("Owner can't be empty!");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(from / size, size, sort);
        return itemRepository.findByOwner_Id(ownerId, page).stream()
                .map(ItemMapper::toItemResponseDto)
                .peek(this::loadBookings)
                .peek(this::loadComments)
                .collect(Collectors.toList());
    }

    private void loadComments(ItemResponseDto itemDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");
        List<Comment> commentList = commentRepository.findByItem_Id(itemDto.getId(), sort);
        if (!commentList.isEmpty()) {
            itemDto.setComments(commentList.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        } else {
            itemDto.setComments(Collections.emptyList());
        }
    }

    @Override
    public List<ItemResponseDto> searchItem(Long userId, String searchText, Integer from, Integer size) {
        if (searchText.isEmpty()) {
            return Collections.emptyList();
        }
        Pageable page = PageRequest.of(from / size, size);
        return itemRepository.search(searchText, page).stream()
                .map(ItemMapper::toItemResponseDto)
                .peek(this::loadComments)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long ownerId, Long itemId, CommentDto commentDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner with id " + ownerId + " is not created!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " is not created!"));

        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        List<Booking> bookings = bookingRepository.findByItem_Id(itemId, sort).stream()
                .filter(booking -> booking.getBooker().equals(owner) &&
                        booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new BadRequestException("Commentator do not have item bookings or booking is not finished");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(owner, item, commentDto)));
    }
}

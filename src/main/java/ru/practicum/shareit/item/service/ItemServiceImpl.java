package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
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

    @Override
    public ItemResponseDto saveItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner with id " + ownerId + " is not created!"));
        Item item = ItemMapper.toItem(owner, itemDto);
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
        List<Booking> bookingList = bookingRepository.findByItem_IdOrderByStartAsc(itemResponseDto.getId());
        List<Booking> lastBookingList = bookingList.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                        !booking.getStatus().equals(BookingStateEnum.REJECTED))
                .collect(Collectors.toList());
        if (!lastBookingList.isEmpty()) {
            Booking lastBooking = lastBookingList.get(lastBookingList.size() - 1);
            itemResponseDto.setLastBooking(new ItemResponseDto.Booking(
                    lastBooking.getId(), lastBooking.getStart(), lastBooking.getEnd(), lastBooking.getBooker().getId(),
                    lastBooking.getStatus(), lastBooking.getCreationTime()));
        }
        List<Booking> nextBookingList = bookingList.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) &&
                        !booking.getStatus().equals(BookingStateEnum.REJECTED))
                .collect(Collectors.toList());
        if (!nextBookingList.isEmpty()) {
            Booking nextBooking = nextBookingList.get(0);
            itemResponseDto.setNextBooking(new ItemResponseDto.Booking(
                    nextBooking.getId(), nextBooking.getStart(), nextBooking.getEnd(), nextBooking.getBooker().getId(),
                    nextBooking.getStatus(), nextBooking.getCreationTime()));
        }
    }

    @Override
    public List<ItemResponseDto> getAllOwnerItems(Long ownerId) {
        if (ownerId == null) {
            throw new BadRequestException("Owner can't be empty!");
        }
        return itemRepository.findByOwner_IdOrderByIdAsc(ownerId).stream()
                .map(ItemMapper::toItemResponseDto)
                .peek(this::loadBookings)
                .peek(this::loadComments)
                .collect(Collectors.toList());
    }

    private void loadComments(ItemResponseDto itemDto) {
        List<Comment> commentList = commentRepository.findByItem_IdOrderByCreationTimeDesc(itemDto.getId());
        if (!commentList.isEmpty()) {
            itemDto.setComments(commentList.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        } else {
            itemDto.setComments(Collections.emptyList());
        }
    }

    @Override
    public List<ItemResponseDto> searchItem(Long userId, String searchText) {
        if (searchText.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.search(searchText, userId).stream()
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

        List<Booking> bookings = bookingRepository.findByItem_IdOrderByStartAsc(itemId).stream()
                .filter(booking -> booking.getBooker().equals(owner) &&
                        booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new BadRequestException("Commentator do not have item bookings or booking is not finished");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(owner, item, commentDto)));
    }
}

package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.enums.RequestStateEnum;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto saveBooking(Long ownerId, BookingRequestDto bookingRequestDto) {
        User booker = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id - " + ownerId + " not found"));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item with id - " + bookingRequestDto.getItemId() + " not found"));
        if (ownerId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Owner can't book his own item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item " + item + " unavailable for booking");
        }
        Booking booking = BookingMapper.toBooking(booker, item, bookingRequestDto);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto considerBooking(Long ownerId, Boolean approved, Long bookingId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id - " + ownerId + " not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id - " + bookingId + " not found"));
        if (!booking.getStatus().equals(BookingStateEnum.WAITING)) {
            throw new BadRequestException("Owner can't change booking status after its considering");
        }
        if (!user.equals(booking.getItem().getOwner())) {
            throw new NotFoundException("Requester and booking owner not equals");
        }
        if (approved) {
            booking.setStatus(BookingStateEnum.APPROVED);
        } else {
            booking.setStatus(BookingStateEnum.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - " + userId + " not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id - " + bookingId + " not found"));
        if (!requester.equals(booking.getBooker()) && !requester.equals(booking.getItem().getOwner())) {
            throw new NotFoundException("Requester and booking or item owner not equals");
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingResponseDto> getAllUserBookings(Long userId, RequestStateEnum state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - " + userId + " not found"));
        List<BookingResponseDto> resultList = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");
        Pageable page = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                resultList = bookingRepository.findByBooker(user, page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case PAST:
                resultList = bookingRepository
                        .findByBookerAndEndBefore(user, LocalDateTime.now(), page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                resultList = bookingRepository
                        .findByBookerAndStartAfter(user, LocalDateTime.now(), page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                sort = Sort.by(Sort.Direction.ASC, "creationTime");
                page = PageRequest.of(from / size, size, sort);
                resultList = bookingRepository.findAllWithCurrentState(user, LocalDateTime.now(), page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                resultList = bookingRepository
                        .findByBookerAndStatus(user, BookingStateEnum.WAITING, page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                resultList = bookingRepository
                        .findByBookerAndStatus(user, BookingStateEnum.REJECTED, page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
        }
        return resultList;
    }

    @Override
    public List<BookingResponseDto> getAllItemsBooking(Long userId, RequestStateEnum state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - " + userId + " not found"));
        if (itemRepository.countByOwner(user) == 0) {
            return Collections.emptyList();
        }
        List<BookingResponseDto> resultList = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");
        Pageable page = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                resultList = bookingRepository.findByItem_Owner(user, page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case PAST:
                resultList = bookingRepository
                        .findByItem_OwnerAndEndBefore(user, LocalDateTime.now(), page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                resultList = bookingRepository
                        .findByItem_OwnerAndStartAfter(user, LocalDateTime.now(), page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                sort = Sort.by(Sort.Direction.ASC, "creationTime");
                page = PageRequest.of(from / size, size, sort);
                resultList = bookingRepository
                        .findAllItemsBookingWithCurrentState(user, LocalDateTime.now(), page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                resultList = bookingRepository
                        .findByItem_OwnerAndStatus(user, BookingStateEnum.WAITING, page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                resultList = bookingRepository
                        .findByItem_OwnerAndStatus(user, BookingStateEnum.REJECTED, page).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
        }
        return resultList;
    }
}

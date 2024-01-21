package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
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
@Validated
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDtoResponse saveBooking(Long ownerId, BookingDtoRequest bookingDtoRequest) {
        User booker = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id - " + ownerId + " not found"));
        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item with id - " + bookingDtoRequest.getItemId() + " not found"));
        if (ownerId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Owner can't book his own item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item " + item + " unavailable for booking");
        }
        if (bookingDtoRequest.getStart().isEqual(bookingDtoRequest.getEnd()) ||
                bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new BadRequestException("Timestamps must not be equal or end before start");
        }
        Booking booking = BookingMapper.fromBookingDtoRequest(booker, item, bookingDtoRequest);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse considerBooking(Long ownerId, Boolean approved, Long bookingId) {
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
    public BookingDtoResponse getBooking(Long userId, Long bookingId) {
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
    public List<BookingDtoResponse> getAllUserBookings(Long userId, RequestStateEnum state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - " + userId + " not found"));
        List<BookingDtoResponse> resultList = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");
        switch (state) {
            case ALL:
                resultList = bookingRepository.findByBooker(user, sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case PAST:
                resultList = bookingRepository
                        .findByBookerAndEndBefore(user, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                resultList = bookingRepository
                        .findByBookerAndStartAfter(user, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                sort = Sort.by(Sort.Direction.ASC, "creationTime");
                resultList = bookingRepository.findAllWithCurrentState(user, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                resultList = bookingRepository
                        .findByBookerAndStatus(user, BookingStateEnum.WAITING, sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                resultList = bookingRepository
                        .findByBookerAndStatus(user, BookingStateEnum.REJECTED, sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
        }
        return resultList;
    }

    @Override
    public List<BookingDtoResponse> getAllItemsBooking(Long userId, RequestStateEnum state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - " + userId + " not found"));
        if (itemRepository.countByOwner(user) == 0) {
            return Collections.emptyList();
        }
        List<BookingDtoResponse> resultList = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");
        switch (state) {
            case ALL:
                resultList = bookingRepository.findByItem_Owner(user, sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case PAST:
                resultList = bookingRepository
                        .findByItem_OwnerAndEndBefore(user, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                resultList = bookingRepository
                        .findByItem_OwnerAndStartAfter(user, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                sort = Sort.by(Sort.Direction.ASC, "creationTime");
                resultList = bookingRepository.findAllItemsBookingWithCurrentState(user, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                resultList = bookingRepository
                        .findByItem_OwnerAndStatus(user, BookingStateEnum.WAITING, sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                resultList = bookingRepository
                        .findByItem_OwnerAndStatus(user, BookingStateEnum.REJECTED, sort).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
                break;
        }
        return resultList;
    }
}

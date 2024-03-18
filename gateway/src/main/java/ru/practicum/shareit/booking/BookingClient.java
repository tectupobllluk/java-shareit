package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.enums.RequestStateEnum;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveBooking(Long ownerId, BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd()) ||
                bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new BadRequestException("Timestamps must not be equal or end before start");
        }
        return post("", ownerId, bookingRequestDto);
    }

    public ResponseEntity<Object> considerBooking(Long ownerId, Boolean approved, Long bookingId) {
        return patch("/" + bookingId + "?approved={approved}",
                ownerId, Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllUserBookings(Long userId, RequestStateEnum state, Integer from, Integer size) {
        return get("?state={state}&from={from}&size={size}", userId,
                Map.of("state", state, "from", from, "size", size));
    }

    public ResponseEntity<Object> getAllItemsBooking(Long userId, RequestStateEnum state, Integer from, Integer size) {
        return get("/owner?state={state}&from={from}&size={size}", userId,
                Map.of("state", state, "from", from, "size", size));
    }
}

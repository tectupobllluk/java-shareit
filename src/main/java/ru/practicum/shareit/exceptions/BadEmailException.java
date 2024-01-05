package ru.practicum.shareit.exceptions;

public class BadEmailException extends RuntimeException {

    public BadEmailException(String message) {
        super(message);
    }
}

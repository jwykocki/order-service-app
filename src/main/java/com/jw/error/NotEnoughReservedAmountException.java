package com.jw.error;

public class NotEnoughReservedAmountException extends RuntimeException {
    public NotEnoughReservedAmountException(String message) {
        super(message);
    }
}

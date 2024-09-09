package com.jw.error;

public class ProductNotReservedException extends RuntimeException {

    public ProductNotReservedException(String message) {
        super(message);
    }
}

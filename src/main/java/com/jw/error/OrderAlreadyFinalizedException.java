package com.jw.error;

public class OrderAlreadyFinalizedException extends RuntimeException {
    public OrderAlreadyFinalizedException(String message
    ) {
        super(message);
    }
}

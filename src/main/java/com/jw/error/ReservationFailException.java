package com.jw.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReservationFailException extends RuntimeException {
    private final String message;
}

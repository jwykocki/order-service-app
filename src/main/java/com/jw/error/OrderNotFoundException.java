package com.jw.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OrderNotFoundException extends RuntimeException {
    private final String message;
}

package com.jw.error;

import java.util.List;
import lombok.Getter;

@Getter
public class InvalidOrderRequestException extends RuntimeException {
    private List<String> violations;
    private String message;

    public InvalidOrderRequestException(String message, List<String> violations) {
        this.violations = violations;
        this.message = message;
    }
}

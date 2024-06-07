package com.jw.error;

import java.util.List;
import lombok.Getter;

@Getter
public class ErrorResponse {
    String message;
    List<String> errors;

    public ErrorResponse(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }
}

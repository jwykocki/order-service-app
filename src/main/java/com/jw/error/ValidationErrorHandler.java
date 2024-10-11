package com.jw.error;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class ValidationErrorHandler implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException exception) {
        log.error("Validation failed: {}", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                        new ErrorResponse(
                                "Request body is not valid", List.of((exception.getMessage()))))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

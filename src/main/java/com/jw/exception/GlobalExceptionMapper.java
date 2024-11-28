package com.jw.exception;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Slf4j
public class GlobalExceptionMapper {

    @ServerExceptionMapper
    public Response handleValidationException(ValidationException exception) {
        log.error("Validation failed: {}", exception.getMessage());
        return getResponse(Response.Status.BAD_REQUEST, exception.getMessage());
    }

    @ServerExceptionMapper
    public Response handleBadOrderRequestException(BadOrderRequestException exception) {
        log.error("An bad request error occurred: {}", exception.getMessage());
        return getResponse(Response.Status.BAD_REQUEST, exception.getMessage());
    }

    @ServerExceptionMapper
    public Response handleOrderNotFoundException(OrderNotFoundException exception) {
        log.error("Order not found: {}", exception.getMessage());
        return getResponse(Response.Status.NOT_FOUND, exception.getMessage());
    }

    @ServerExceptionMapper
    public Response handleGenericException(Throwable exception) {
        log.error("An error occurred: {}", exception.getMessage());
        return getResponse(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private static Response getResponse(Response.Status status, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(status.getReasonPhrase(), List.of(message)))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

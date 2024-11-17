package com.jw.error;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Slf4j
public class GlobalExceptionHandler {

    @ServerExceptionMapper
    public Response handleInvalidOrderRequestException(InvalidOrderRequestException exception) {
        log.error("Invalid order request: {}", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage(), exception.getViolations()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @ServerExceptionMapper
    public Response handleValidationException(ValidationException exception) {
        log.error("Validation failed: {}", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                        new ErrorResponse(
                                "Request body is not valid", List.of((exception.getMessage()))))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @ServerExceptionMapper
    public Response handleReservationFailException(ReservationFailException exception) {
        log.error("Reservation failed: {}", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage(), Collections.emptyList()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @ServerExceptionMapper
    public Response handleOrderNotFoundException(OrderNotFoundException exception) {
        log.error("Order not found: {}", exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(exception.getMessage(), Collections.emptyList()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @ServerExceptionMapper
    public Response handleOrderAlreadyFinalized(OrderAlreadyFinalizedException exception) {
        log.error("Order already finalized: {}", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage(), Collections.emptyList()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @ServerExceptionMapper
    public Response handleGenericException(Throwable exception) {
        log.error("An error occurred: {}", exception.getMessage());
        log.info(exception.getMessage(), exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("An error occurred", Collections.emptyList()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

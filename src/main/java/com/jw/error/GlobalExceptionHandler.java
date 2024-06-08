package com.jw.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof InvalidOrderRequestException) {
            return handleInvalidOrderRequestException((InvalidOrderRequestException) exception);
        } else if (exception instanceof OrderNotFoundException) {
            return handleOrderNotFoundException((OrderNotFoundException) exception);
        } else {
            return handleGenericException(exception);
        }
    }

    private Response handleInvalidOrderRequestException(InvalidOrderRequestException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage(), exception.getViolations()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response handleOrderNotFoundException(OrderNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(exception.getMessage(), Collections.emptyList()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response handleGenericException(Throwable exception) {
        exception.printStackTrace();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("An error occurred", Collections.emptyList()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

package com.jw.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof InvalidOrderRequestException) {
            return handleInvalidOrderRequestException((InvalidOrderRequestException) exception);
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

    private Response handleGenericException(Throwable exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage(), List.of(exception.getMessage())))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

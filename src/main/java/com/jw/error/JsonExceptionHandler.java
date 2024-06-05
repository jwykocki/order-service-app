package com.jw.error;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class JsonExceptionHandler implements ExceptionMapper<JsonParseException> {
    @Override
    public Response toResponse(JsonParseException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage(), List.of(e.getMessage())))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

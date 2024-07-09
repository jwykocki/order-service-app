package com.jw.resources;

import com.jw.dto.reservation.ProductReservationRequest;
import com.jw.dto.reservation.ReservationResult;
import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "reservation")
@Default
public interface ReservationResource {

    @POST
    @Path("/reserve")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ReservationResult sendReservationRequest(ProductReservationRequest productReservationRequest);
}

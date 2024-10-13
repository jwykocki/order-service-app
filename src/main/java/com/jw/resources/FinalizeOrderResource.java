package com.jw.resources;

import com.jw.dto.finalize.request.OrderFinalizeRequest;
import com.jw.dto.finalize.request.OrderFinalizeResponse;
import com.jw.service.FinalizeOrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Path("/order/finalize")
@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class FinalizeOrderResource {

    private final FinalizeOrderService finalizeOrderService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrderFinalizeResponse finalizeOrder(OrderFinalizeRequest finalizeRequest) {
        log.info("Received finalize order request");
        OrderFinalizeResponse response = finalizeOrderService.finalizeOrder(finalizeRequest);
        log.info(
                "Successfully processed finalize order request (id = {})",
                finalizeRequest.orderId());
        return response;
    }
}

package com.jw.resources;

import com.jw.dto.finalize.request.OrderFinalizeRequest;
import com.jw.dto.finalize.request.OrderFinalizeResponse;
import com.jw.dto.response.OrderResponse;
import com.jw.service.FinalizeOrderService;
import com.jw.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Path("/order/finalize")
@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class FinalizeOrderResources {

    private final OrderService orderService;
    private final FinalizeOrderService finalizeOrderService;

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrderResponse getOrderByOderId(@PathParam("id") Long id) {
        log.info("Received get order request (id = {})", id);
        OrderResponse orderResponse = orderService.getOrderById(id);
        log.info("Successfully processed get order request (id = {})", id);
        return orderResponse;
    }

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

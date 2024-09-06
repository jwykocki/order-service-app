package com.jw.resources;

import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.dto.response.OrdersResponse;
import com.jw.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Path("/order")
@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class OrderResource {

    private final OrderService orderService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrdersResponse getAllOrders() {
        log.info("Received get all orders request");
        OrdersResponse ordersResponse = new OrdersResponse(orderService.getAllOrders());
        log.info("Successfully processed get all orders request");
        return ordersResponse;
    }

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
    public OrderResponse saveOrder(@Valid OrderRequest orderRequest) {
        log.info("Received save order request");
        OrderResponse orderResponse = orderService.processOrderRequest(orderRequest);
        log.info("Successfully processed save order request (id = {})", orderResponse.orderId());
        return orderResponse;
    }

    @DELETE
    @Path("/{id}")
    public void deleteOrder(@PathParam("id") Long id) {
        log.info("Received delete order request (id = {})", id);
        orderService.deleteOrder(id);
        log.info("Successfully processed delete order request (id = {})", id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrderResponse updateOrder(@PathParam("id") Long id, OrderRequest orderRequest) {
        log.info("Received update order request (id = {})", id);
        OrderResponse orderResponse = orderService.processUpdateOrder(id, orderRequest);
        log.info("Sucessfully processed update order request (id = {})", id);
        return orderResponse;
    }
}

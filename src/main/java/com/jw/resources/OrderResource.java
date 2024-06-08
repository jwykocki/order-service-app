package com.jw.resources;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.dto.OrdersResponse;
import com.jw.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("/order")
@RequiredArgsConstructor
@ApplicationScoped
public class OrderResource {

    private final OrderService orderService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrdersResponse getAllOrders() {
        return new OrdersResponse(orderService.getAllOrders());
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrderResponse getOrderByOderId(@PathParam("id") Long id) {
        return orderService.getOrderById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void saveOrder(OrderRequest orderRequest) {
        orderService.saveOrder(orderRequest);
    }

    @DELETE
    @Path("/{id}")
    public void deleteOrder(@PathParam("id") Long id) {
        orderService.deleteOrder(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void updateOrder(OrderRequest orderRequest) {
        orderService.updateOrder(orderRequest);
    }
}

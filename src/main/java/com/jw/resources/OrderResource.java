package com.jw.resources;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Path("/order")
@RequiredArgsConstructor
@ApplicationScoped
public class OrderResource {

    private final OrderService orderService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void saveOrder(OrderRequest orderRequest) {
        orderService.saveOrder(orderRequest);
    }
}

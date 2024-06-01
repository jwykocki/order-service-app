package com.jw.resources;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.entity.OrderDao;
import com.jw.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import java.util.List;


@Path("/order")
@RequiredArgsConstructor
@ApplicationScoped
public class OrderResource {

    private final OrderService orderService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllorders();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void saveOrder(OrderRequest orderRequest) {
        orderService.saveOrder(orderRequest);
    }
}

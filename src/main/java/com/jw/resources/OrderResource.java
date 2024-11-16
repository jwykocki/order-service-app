package com.jw.resources;

import com.jw.dto.finalize.request.OrderFinalizeResponse;
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
//REVIEW-VINI: I liked this class a lot, pretty simple how a controller should be :)
public class OrderResource {

    private final OrderService orderService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrdersResponse getAllOrders() {
        //REVIEW-VINI: We can change these logs to debug, small explanation why - this is my opinion :) -: nowadays most of the companies/services has a good metrics and trace for our services
        // which make un-necessary such logs to check if a request was received/process or not, plus in case your app is high demand then these logs can be a bit of noise.
        log.info("Received get all orders request");
        OrdersResponse ordersResponse = new OrdersResponse(orderService.getAllOrders());
        log.info("Successfully processed get all orders request");
        //REVIEW-VINI: Once you fix the logs here, maybe we can inline all these service calls? For instance: return new OrdersResponse(orderService.getAllOrders());
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
    //REVIEW-VINI: Should this method be present on FinalizeOrderResource? IMHO delete order is also a way to finalize an order
    public OrderFinalizeResponse deleteOrder(@PathParam("id") Long id) {
        log.info("Received delete order request (id = {})", id);
        OrderFinalizeResponse orderFinalizeResponse = orderService.deleteOrder(id);
        log.info("Successfully processed delete order request (id = {})", id);
        return orderFinalizeResponse;
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

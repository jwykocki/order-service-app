package com.jw.service;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderMapper {
    public OrderResponse mapOrderToOrderResponse(com.jw.entity.Order order) {
        return OrderResponse.builder().name(order.getName()).id(order.getId()).build();
    }

    public com.jw.entity.Order mapOrderRequestToOrder(OrderRequest orderRequest) {
        return com.jw.entity.Order.builder().name(orderRequest.name()).build();
    }
}

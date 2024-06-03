/* (C)2024 */
package com.jw.dto;

import com.jw.entity.Order;

public class OrderMapper {
    public static OrderResponse mapOrdertoOrderResponse(Order order) {
        return OrderResponse.builder().name(order.getName()).id(order.getId()).build();
    }

    public static Order mapOrderRequestToOrder(OrderRequest orderRequest) {
        return Order.builder().name(orderRequest.name()).build();
    }
}

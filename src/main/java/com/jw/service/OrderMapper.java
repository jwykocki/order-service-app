package com.jw.service;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface OrderMapper {
    Order toOrder(OrderRequest orderRequest);
    OrderResponse toOrderResponse(Order order);
}

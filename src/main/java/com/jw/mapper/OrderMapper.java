package com.jw.mapper;

import com.jw.dto.finalize.request.*;
import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.dto.unprocessed.orders.OrderProductQueue;
import com.jw.dto.unprocessed.orders.UnprocessedOrderQueue;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface OrderMapper {
    Order toOrder(OrderRequest orderRequest);

    OrderResponse toOrderResponse(Order order);

    UnprocessedOrderQueue toUnprocessedOrderQueue(Order order);

    OrderProductQueue toOrderProductQueue(OrderProduct orderProduct);

    OrderProductFinalizeResponse toOrderProductFinalizeResponse(
            FinalizedProductQueue finalizedProductQueue);

    void update(@MappingTarget Order order, OrderRequest updateOrderRequest);
}

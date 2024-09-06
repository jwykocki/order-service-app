package com.jw.service;

import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.dto.reservation.ProductReservationRequest;
import com.jw.dto.unprocessed.orders.OrderProductQueue;
import com.jw.dto.unprocessed.orders.UnprocessedOrderQueue;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface OrderMapper {
    Order toOrder(OrderRequest orderRequest);

    OrderResponse toOrderResponse(Order order);

    ProductReservationRequest toProductReservationRequest(Order order);

    UnprocessedOrderQueue toUnprocessedOrderQueue(Order order);

    OrderProductQueue toOrderProductQueue(OrderProduct orderProduct);
}

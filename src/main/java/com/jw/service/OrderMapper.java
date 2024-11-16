package com.jw.service;

import com.jw.dto.finalize.request.*;
import com.jw.dto.request.OrderRequest;
import com.jw.dto.reservation.ProductReservationRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.dto.unprocessed.orders.OrderProductQueue;
import com.jw.dto.unprocessed.orders.UnprocessedOrderQueue;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface OrderMapper {
    Order toOrder(OrderRequest orderRequest);

    OrderResponse toOrderResponse(Order order);

    //REVIEW-VINI: Looks like it's never used, we can remove it
    ProductReservationRequest toProductReservationRequest(Order order);

    UnprocessedOrderQueue toUnprocessedOrderQueue(Order order);

    OrderProductQueue toOrderProductQueue(OrderProduct orderProduct);

    OrderProductFinalizeResponse toOrderProductFinalizeResponse(
            FinalizedProductQueue finalizedProductQueue);

    //REVIEW-VINI: Looks like it's never used, we can remove it
    @Mapping(source = "orderProducts", target = "products")
    OrderFinalizeRequest toOrderFinalizeRequest(OrderResponse orderResponse);
}

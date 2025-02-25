package com.jw.dto.response;

import com.jw.constants.OrderStatus;
import com.jw.dto.processed.ProductReservationResponse;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long customerId,
        OrderStatus status,
        List<ProductReservationResponse> orderProducts) {}

package com.jw.dto.response;

import com.jw.dto.processed.ProductReservationResponse;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long customerId,
        String status,
        List<ProductReservationResponse> orderProducts) {}

package com.jw.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record OrderResponse(
        Long orderId, Long customerId, String status, List<OrderProductRequest> orderProducts) {}

package com.jw.dto;

import java.util.List;

public record OrderResponse(
        Long orderId, Long customerId, String status, List<OrderProductRequest> orderProducts) {}

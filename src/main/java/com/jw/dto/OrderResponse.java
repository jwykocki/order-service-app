package com.jw.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderResponse(Long orderId, Long customerId, String status, List<OrderProductRequest> orderProducts) {}

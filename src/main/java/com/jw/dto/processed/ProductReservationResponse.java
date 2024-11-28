package com.jw.dto.processed;

import com.jw.constants.OrderProductStatus;

public record ProductReservationResponse(
        Long productId, Integer quantity, OrderProductStatus status) {}

package com.jw.dto.processed;

import com.jw.dto.unprocessed.orders.OrderProductQueue;

public record ProductReservationResult(Long orderId, OrderProductQueue product, String status) {}

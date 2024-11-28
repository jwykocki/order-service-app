package com.jw.dto.reservation;

import com.jw.constants.OrderStatus;

public record ReservationResult(Long orderId, OrderStatus status, String message) {}

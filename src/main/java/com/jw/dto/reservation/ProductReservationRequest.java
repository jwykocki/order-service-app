package com.jw.dto.reservation;

import com.jw.dto.OrderProductRequest;
import java.util.List;

public record ProductReservationRequest(Long orderId, List<OrderProductRequest> orderProducts) {}

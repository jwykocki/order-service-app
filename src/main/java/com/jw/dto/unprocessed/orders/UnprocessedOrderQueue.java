package com.jw.dto.unprocessed.orders;

import com.jw.constants.OrderStatus;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import lombok.*;

@RegisterForReflection
public record UnprocessedOrderQueue(
        Long orderId, OrderStatus status, List<OrderProductQueue> orderProducts) {}

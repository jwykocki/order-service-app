package com.jw.dto.unprocessed.orders;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import lombok.*;

@RegisterForReflection
public record UnprocessedOrderQueue(
        Long orderId, String status, List<OrderProductQueue> orderProducts) {}

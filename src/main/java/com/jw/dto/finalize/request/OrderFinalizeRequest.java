package com.jw.dto.finalize.request;

import java.util.List;

public record OrderFinalizeRequest(
        Long orderId, Long customerId, List<OrderProductFinalizeRequest> products) {}

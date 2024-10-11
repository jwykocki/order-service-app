package com.jw.dto.finalize.request;

import java.util.List;

public record OrderFinalizeResponse(
        Long orderId, Long customerId, List<OrderProductFinalizeResponse> finalizedProducts) {}

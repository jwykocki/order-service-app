package com.jw.dto.finalize.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderFinalizeRequest(
        @NotNull Long orderId,
        Long customerId,
        @NotNull List<@Valid OrderProductFinalizeRequest> products) {}

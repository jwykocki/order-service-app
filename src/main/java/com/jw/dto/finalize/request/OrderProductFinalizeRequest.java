package com.jw.dto.finalize.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderProductFinalizeRequest(
        @Min(0) @NotNull Long productId, @Min(0) @NotNull Integer toFinalize) {}

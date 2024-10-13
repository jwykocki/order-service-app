package com.jw.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderProductRequest(@NotNull Long productId, @Min(1) @NotNull int quantity) {}

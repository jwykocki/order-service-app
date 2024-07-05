package com.jw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderProductRequest(Long productId, @Min(1) int quantity) {}

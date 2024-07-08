package com.jw.dto;

import jakarta.validation.constraints.Min;

public record OrderProductRequest(Long productId, @Min(1) int quantity) {}

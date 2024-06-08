package com.jw.dto;

import lombok.Builder;

@Builder
public record OrderResponse(Long id, String name) {}

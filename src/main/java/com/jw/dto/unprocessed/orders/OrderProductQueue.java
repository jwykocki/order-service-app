package com.jw.dto.unprocessed.orders;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@RegisterForReflection
public record OrderProductQueue(Long productId, Integer quantity) {}

package com.jw.dto.finalize.request;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record FinalizedOrderQueue(Long orderId, FinalizedProductQueue product) {}

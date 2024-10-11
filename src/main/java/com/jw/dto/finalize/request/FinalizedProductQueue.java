package com.jw.dto.finalize.request;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record FinalizedProductQueue(Long productId, int reserved, int finalized) {}

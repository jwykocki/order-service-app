package com.jw.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest(
        @NotNull Long customerId, @NotEmpty List<@Valid OrderProductRequest> orderProducts) {}

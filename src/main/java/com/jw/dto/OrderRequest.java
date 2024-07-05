package com.jw.dto;

import java.util.List;

public record OrderRequest(Long customerId, List<ProductOrderRequest> productOrderRequests) {}

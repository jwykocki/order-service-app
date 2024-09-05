package com.jw.dto.unprocessed.products;

import com.jw.dto.unprocessed.orders.OrderProductQueue;

public record UnprocessedProductQueue(Long orderId, OrderProductQueue product) {}

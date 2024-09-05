package com.jw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.dto.processed.ProductReservationResult;
import com.jw.dto.unprocessed.orders.UnprocessedOrderQueue;
import com.jw.dto.unprocessed.products.UnprocessedProductQueue;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class QueueProcessor {

    private final QueueService queueService;
    private final ProductService productService;

    @Incoming("unprocessed-orders")
    public void stream(UnprocessedOrderQueue order) {
        log.info("Processing: {}", order);
        List<UnprocessedProductQueue> list =
                order.orderProducts().stream()
                        .map(p -> new UnprocessedProductQueue(order.orderId(), p))
                        .toList();
        list.forEach(queueService::saveProduct);
    }

    @Incoming("processed-products")
    public void streamProducts(byte[] product) throws JsonProcessingException {
        String value = new String(product, StandardCharsets.UTF_8);
        log.info("Received processed: {}", value);
        ProductReservationResult productReservationResult =
                new ObjectMapper().readValue(value, ProductReservationResult.class);
        log.info("Mapped: {}", productReservationResult);
        productService.updateOrderProductStatus(productReservationResult);
    }
}

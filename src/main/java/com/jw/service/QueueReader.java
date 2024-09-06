package com.jw.service;

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
public class QueueReader {

    private final QueueWriter queueWriter;
    private final ProductService productService;
    private final OrderProductMapper mapper;

    @Incoming("unprocessed-orders")
    public void readUnprocessedOrders(UnprocessedOrderQueue order) {
        log.info("Processing: {}", order);
        List<UnprocessedProductQueue> unprocessedProducts =
                order.orderProducts().stream()
                        .map(p -> new UnprocessedProductQueue(order.orderId(), p))
                        .toList();
        unprocessedProducts.forEach(queueWriter::saveProductOnUnprocessedProducts);
    }

    @Incoming("processed-products")
    public void readProcessedProducts(byte[] product) {
        String value = new String(product, StandardCharsets.UTF_8);
        productService.updateOrderProductStatus(mapper.toProductReservationResult(value));
    }
}

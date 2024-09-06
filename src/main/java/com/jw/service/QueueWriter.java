package com.jw.service;

import com.jw.dto.unprocessed.orders.UnprocessedOrderQueue;
import com.jw.dto.unprocessed.products.UnprocessedProductQueue;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Slf4j
@ApplicationScoped
public class QueueWriter {

    @Channel("unprocessed-orders")
    @Broadcast
    Emitter<UnprocessedOrderQueue> unprocessedOrdersEmitter;

    @Channel("unprocessed-products")
    @Broadcast
    Emitter<UnprocessedProductQueue> unprocessedProductsEmitter;

    public void saveOrderOnUnprocessedOrders(UnprocessedOrderQueue order) {
        log.info("Saving order on unprocessed-orders queue (id = {})", order.orderId());
        unprocessedOrdersEmitter.send(order);
    }

    public void saveProductOnUnprocessedProducts(UnprocessedProductQueue product) {
        log.info("Saving product on unprocessed-products (id = {})", product.orderId());
        unprocessedProductsEmitter.send(product);
    }
}

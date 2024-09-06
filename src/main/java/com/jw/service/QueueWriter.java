package com.jw.service;

import com.jw.dto.unprocessed.orders.UnprocessedOrderQueue;
import com.jw.dto.unprocessed.products.UnprocessedProductQueue;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class QueueWriter {

    @Channel("unprocessed-orders")
    @Broadcast
    Emitter<UnprocessedOrderQueue> unprocessedOrdersEmitter;

    @Channel("unprocessed-products")
    @Broadcast
    Emitter<UnprocessedProductQueue> unprocessedProductsEmitter;

    public void saveOrderOnUnprocessedOrders(UnprocessedOrderQueue order) {
        unprocessedOrdersEmitter.send(order);
    }

    public void saveProductOnUnprocessedProducts(UnprocessedProductQueue product) {
        unprocessedProductsEmitter.send(product);
    }
}

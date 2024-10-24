package com.jw.service;

import static com.jw.constants.OrderStatus.ALL_AVAILABLE;

import com.jw.dto.processed.ProductReservationResult;
import com.jw.dto.response.OrderResponse;
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
    private final OrderService orderService;
    private final OrderProductMapper orderProductMapper;
    private final OrderMapper orderMapper;
    private final FinalizeOrderService finalizeOrderService;

    @Incoming("unprocessed-orders")
    public void readUnprocessedOrders(UnprocessedOrderQueue order) {
        log.info("Received order from unprocessed-orders queue (id = {})", order.orderId());
        List<UnprocessedProductQueue> unprocessedProducts =
                order.orderProducts().stream()
                        .map(p -> new UnprocessedProductQueue(order.orderId(), p))
                        .toList();
        unprocessedProducts.forEach(queueWriter::saveProductOnUnprocessedProducts);
    }

    @Incoming("processed-products")
    public void readProcessedProducts(byte[] product) {
        String value = new String(product, StandardCharsets.UTF_8);
        ProductReservationResult reservationResult =
                orderProductMapper.toProductReservationResult(value);
        log.info(
                "Received reservation result from processed-products queue (id = {})",
                reservationResult.orderId());
        productService.updateOrderProductStatus(reservationResult);
        String status = orderService.updateOrderStatusAndReturn(reservationResult.orderId());
        if (status.equals(ALL_AVAILABLE)) {
            log.info(
                    "All products are available, finalizing order (id = {})",
                    reservationResult.orderId());
            OrderResponse orderResponse = orderService.getOrderById(reservationResult.orderId());
            finalizeOrderService.finalizeOrder(
                    orderProductMapper.getFinalizeRequestFromOrderResponse(orderResponse));
        }
    }
}

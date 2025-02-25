package com.jw.service;

import static com.jw.exception.ExceptionMessages.*;

import com.jw.constants.OrderStatus;
import com.jw.dto.processed.ProductReservationResult;
import com.jw.dto.unprocessed.orders.UnprocessedOrderQueue;
import com.jw.dto.unprocessed.products.UnprocessedProductQueue;
import com.jw.entity.Order;
import com.jw.exception.OrderNotFoundException;
import com.jw.mapper.OrderMapper;
import com.jw.mapper.OrderProductMapper;
import com.jw.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final FinalizeOrderService finalizeOrderService;

    @Incoming("unprocessed-orders")
    public void readUnprocessedOrders(UnprocessedOrderQueue order) {
        log.debug("Received order from unprocessed-orders queue (id = {})", order.orderId());
        List<UnprocessedProductQueue> unprocessedProducts =
                order.orderProducts().stream()
                        .map(p -> new UnprocessedProductQueue(order.orderId(), p))
                        .toList();
        unprocessedProducts.forEach(queueWriter::saveProductOnUnprocessedProducts);
    }

    @Incoming("processed-products")
    @Transactional
    public void readProcessedProducts(byte[] product) {
        String value = new String(product, StandardCharsets.UTF_8);
        ProductReservationResult reservationResult =
                orderProductMapper.toProductReservationResult(value);
        log.debug(
                "Received reservation result from processed-products queue (id = {})",
                reservationResult.orderId());
        productService.updateOrderProductStatus(reservationResult);
        OrderStatus status = orderService.updateOrderStatusAndReturn(reservationResult.orderId());
        if (OrderStatus.ALL_AVAILABLE.equals(status)) {
            log.debug(
                    "All products are available, finalizing order (id = {})",
                    reservationResult.orderId());
            Order order =
                    orderRepository
                            .findByIdOptional(reservationResult.orderId())
                            .orElseThrow(
                                    () ->
                                            new OrderNotFoundException(
                                                    ORDER_NOT_FOUND_MESSAGE.formatted(
                                                            reservationResult.orderId())));
            finalizeOrderService.finalizeOrder(
                    orderProductMapper.getFinalizeRequestFromOrder(order));
        }
    }
}

package com.jw.service;

import com.jw.constants.OrderProductStatus;
import com.jw.dto.finalize.request.*;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import com.jw.error.NotEnoughReservedAmountException;
import com.jw.error.OrderNotFoundException;
import com.jw.error.ProductNotReservedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class FinalizeOrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final QueueWriter queueWriter;

    @Transactional
    public OrderFinalizeResponse finalizeOrder(OrderFinalizeRequest request) {
        // check if exists
        Order order = findOrderOrElseThrowException(request.orderId());
        // check products correctness
        List<FinalizedOrderQueue> finalizedProductsQueue =
                createFinalizedOrderQueue(
                        order.getOrderId(), request.products(), order.getOrderProducts());
        // put finalized order on queue
        finalizedProductsQueue.forEach(queueWriter::saveProductOnFinalizedProducts);
        // update quantity and status
        finalizedProductsQueue.forEach(p -> updateOrderProduct(p.product(), order));
        order.setStatus("FINALIZED");
        // update order status to FINALIZED
        return new OrderFinalizeResponse(
                request.orderId(),
                request.customerId(),
                toOrderProductFinalizeResponse(finalizedProductsQueue));
    }

    private static void updateOrderProduct(FinalizedProductQueue p, Order order) {
        OrderProduct orderProduct1 =
                order.getOrderProducts().stream()
                        .filter(orderProduct -> orderProduct.getProductId().equals(p.productId()))
                        .findFirst()
                        .get();
        orderProduct1.setQuantity(p.finalized());
        orderProduct1.setStatus("FINALIZED");
    }

    private List<OrderProductFinalizeResponse> toOrderProductFinalizeResponse(
            List<FinalizedOrderQueue> finalizedOrderQueue) {
        return finalizedOrderQueue.stream()
                .map(p -> orderMapper.toOrderProductFinalizeResponse(p.product()))
                .toList();
    }

    private List<FinalizedOrderQueue> createFinalizedOrderQueue(
            Long orderId,
            List<OrderProductFinalizeRequest> toFinalizeProducts,
            List<OrderProduct> reservedProducts) {
        List<FinalizedOrderQueue> finalizedOrdersQueue = new ArrayList<>();
        toFinalizeProducts.forEach(
                toFinalizeProduct -> {
                    OrderProduct reservedProduct =
                            findOrderProductOrElseThrowException(
                                    reservedProducts, toFinalizeProduct.productId());
                    checkIfProductAmountIsCorrect(toFinalizeProduct, reservedProduct);
                    finalizedOrdersQueue.add(
                            new FinalizedOrderQueue(
                                    orderId,
                                    new FinalizedProductQueue(
                                            reservedProduct.getProductId(),
                                            reservedProduct.getQuantity(),
                                            toFinalizeProduct.toFinalize())));
                });
        return finalizedOrdersQueue;
    }

    private OrderProduct findOrderProductOrElseThrowException(
            List<OrderProduct> products, Long productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .filter(p -> p.getStatus().equals(OrderProductStatus.RESERVED))
                .findFirst()
                .orElseThrow(
                        () ->
                                new ProductNotReservedException(
                                        "Product with id %s not reserved".formatted(productId)));
    }

    private void checkIfProductAmountIsCorrect(
            OrderProductFinalizeRequest toFinalizeProduct, OrderProduct reservedProduct) {
        if (toFinalizeProduct.toFinalize() > reservedProduct.getQuantity()) {
            throw new NotEnoughReservedAmountException(
                    "Not enough reserved for product id %s"
                            .formatted(reservedProduct.getProductId()));
        }
    }

    private Order findOrderOrElseThrowException(Long id) {
        return orderRepository
                .findByIdOptional(id)
                .orElseThrow(
                        () ->
                                new OrderNotFoundException(
                                        "Order with id = %s was not found".formatted(id)));
    }
}

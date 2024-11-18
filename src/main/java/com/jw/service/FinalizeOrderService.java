package com.jw.service;

import static com.jw.constants.OrderProductStatus.RESERVED;
import static com.jw.exception.ExceptionMessages.*;

import com.jw.constants.OrderStatus;
import com.jw.dto.finalize.request.*;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import com.jw.exception.BadOrderRequestException;
import com.jw.exception.OrderNotFoundException;
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

        Order order = findOrderOrElseThrowException(request.orderId());
        checkIfOrderWasNotFinalizedBefore(order);
        List<FinalizedOrderQueue> finalizedProductsQueue =
                finalizeOrderAndReturnFinalizedProducts(
                        order.getOrderId(), request.products(), order.getOrderProducts());
        finalizedProductsQueue.forEach(queueWriter::saveProductOnFinalizedProducts);
        finalizedProductsQueue.forEach(p -> updateOrderProduct(p.product(), order));
        order.setStatus(OrderStatus.FINALIZED.name());
        return new OrderFinalizeResponse(
                request.orderId(),
                request.customerId(),
                toOrderProductFinalizeResponse(finalizedProductsQueue));
    }

    public OrderFinalizeResponse deleteProductReservation(Order order) {
        checkIfOrderWasNotFinalizedBefore(order);
        List<FinalizedOrderQueue> toFinalizeProducts =
                order.getOrderProducts().stream()
                        .filter(this::wasProductReserved)
                        .map(
                                orderProduct ->
                                        toEmptyFinalizeOrderQueue(order.getOrderId(), orderProduct))
                        .toList();
        toFinalizeProducts.forEach(queueWriter::saveProductOnFinalizedProducts);
        return new OrderFinalizeResponse(
                order.getOrderId(),
                order.getCustomerId(),
                toOrderProductFinalizeResponse(toFinalizeProducts));
    }

    private boolean wasProductReserved(OrderProduct orderProduct) {
        return orderProduct.getStatus().equals(RESERVED);
    }

    private FinalizedOrderQueue toEmptyFinalizeOrderQueue(Long orderId, OrderProduct orderProduct) {
        return new FinalizedOrderQueue(
                orderId,
                new FinalizedProductQueue(
                        orderProduct.getProductId(), orderProduct.getQuantity(), 0));
    }

    private void checkIfOrderWasNotFinalizedBefore(Order order) {
        if (order.getStatus().equals(OrderStatus.FINALIZED)) {
            throw new BadOrderRequestException(
                    ORDER_ALREADY_FINALIZED_MESSAGE.formatted(order.getOrderId()));
        }
    }

    private static void updateOrderProduct(FinalizedProductQueue p, Order order) {
        OrderProduct orderProduct1 =
                order.getOrderProducts().stream()
                        .filter(orderProduct -> orderProduct.getProductId().equals(p.productId()))
                        .findFirst()
                        .get();
        orderProduct1.setQuantity(p.finalized());
        orderProduct1.setStatus(OrderStatus.FINALIZED.name());
    }

    private List<OrderProductFinalizeResponse> toOrderProductFinalizeResponse(
            List<FinalizedOrderQueue> finalizedOrderQueue) {
        return finalizedOrderQueue.stream()
                .map(p -> orderMapper.toOrderProductFinalizeResponse(p.product()))
                .toList();
    }

    private List<FinalizedOrderQueue> finalizeOrderAndReturnFinalizedProducts(
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
                .filter(p -> p.getStatus().equals(RESERVED))
                .findFirst()
                .orElseThrow(
                        () ->
                                new BadOrderRequestException(
                                        PRODUCT_NOT_RESERVED_MESSAGE.formatted(productId)));
    }

    private void checkIfProductAmountIsCorrect(
            OrderProductFinalizeRequest toFinalizeProduct, OrderProduct reservedProduct) {
        if (toFinalizeProduct.toFinalize() > reservedProduct.getQuantity()) {
            throw new BadOrderRequestException(
                    PRODUCT_NOT_ENOUGH_RESERVED_MESSAGE.formatted(reservedProduct.getProductId()));
        }
    }

    private Order findOrderOrElseThrowException(Long id) {
        return orderRepository
                .findByIdOptional(id)
                .orElseThrow(
                        () -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE.formatted(id)));
    }
}

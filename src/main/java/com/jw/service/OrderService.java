package com.jw.service;

import static com.jw.constants.OrderProductStatus.*;
import static com.jw.exception.ExceptionMessages.ORDER_NOT_FOUND_MESSAGE;

import com.jw.constants.OrderStatus;
import com.jw.dto.finalize.request.OrderFinalizeResponse;
import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import com.jw.exception.OrderNotFoundException;
import com.jw.mapper.OrderMapper;
import com.jw.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final QueueWriter queueWriter;
    private final FinalizeOrderService finalizeOrderService;

    @Transactional
    public OrderResponse processOrderRequest(OrderRequest orderRequest) {
        Order order = createOrderInDatabase(orderRequest);
        log.info("Saved order in database (id = {})", order.getOrderId());
        queueWriter.saveOrderOnUnprocessedOrders(orderMapper.toUnprocessedOrderQueue(order));
        return orderMapper.toOrderResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.listAll().stream().map(orderMapper::toOrderResponse).toList();
    }

    @Transactional
    public OrderFinalizeResponse deleteOrder(Long id) {
        Order order = getOrderOrElseThrowException(id);
        OrderFinalizeResponse orderFinalizeResponse =
                finalizeOrderService.deleteProductReservation(order);
        orderRepository.deleteById(id);
        return orderFinalizeResponse;
    }

    @Transactional
    public OrderResponse getOrderById(Long id) {
        Order order = getOrderOrElseThrowException(id);
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderStatus updateOrderStatusAndReturn(Long orderId) {
        Order order = getOrderOrElseThrowException(orderId);
        if (allRequestedProductsAreProcessed(order)) {
            if (allRequestedProductsReserved(order)) {
                order.setStatus(OrderStatus.ALL_AVAILABLE);
                return OrderStatus.ALL_AVAILABLE;
            } else {
                order.setStatus(OrderStatus.PARTIALLY_AVAILABLE);
                return OrderStatus.PARTIALLY_AVAILABLE;
            }
        }
        order.setStatus(OrderStatus.UNPROCESSED);
        return OrderStatus.UNPROCESSED;
    }

    @Transactional
    public OrderResponse processUpdateOrder(Long orderId, OrderRequest orderRequest) {
        Order order = getOrderOrElseThrowException(orderId);
        orderMapper.update(order, orderRequest);
        return orderMapper.toOrderResponse(order);
    }

    private boolean allRequestedProductsAreProcessed(Order order) {
        return order.getOrderProducts().stream()
                .filter(orderProduct -> UNKNOWN.equals(orderProduct.getStatus()))
                .toList()
                .isEmpty();
    }

    private boolean allRequestedProductsReserved(Order order) {
        List<OrderProduct> reserved =
                order.getOrderProducts().stream()
                        .filter(orderProduct -> RESERVED.equals(orderProduct.getStatus()))
                        .toList();
        return reserved.size() == order.getOrderProducts().size();
    }

    private Order createOrderInDatabase(OrderRequest orderRequest) {
        Order order = orderMapper.toOrder(orderRequest);
        order.getOrderProducts().forEach(p -> p.setStatus(UNKNOWN));
        order.setStatus(OrderStatus.UNPROCESSED);
        orderRepository.persist(order);
        return order;
    }

    private Order getOrderOrElseThrowException(Long id) {
        return orderRepository
                .findByIdOptional(id)
                .orElseThrow(
                        () -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE.formatted(id)));
    }
}

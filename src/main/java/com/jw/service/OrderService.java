package com.jw.service;

import static com.jw.constants.OrderProductStatus.*;
import static com.jw.constants.OrderStatus.*;
import static com.jw.exception.ExceptionMessages.ORDER_NOT_FOUND_MESSAGE;

import com.jw.constants.OrderStatus;
import com.jw.dto.finalize.request.OrderFinalizeResponse;
import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import com.jw.exception.OrderNotFoundException;
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
    public String updateOrderStatusAndReturn(Long orderId) {
        Order order = getOrderOrElseThrowException(orderId);
        if (allRequestedProductsAreProcessed(order)) {
            if (allRequestedProductsReserved(order)) {
                order.setStatus(OrderStatus.ALL_AVAILABLE.name());
                return OrderStatus.ALL_AVAILABLE.name();
            } else {
                order.setStatus(OrderStatus.PARTIALLY_AVAILABLE.name());
                return OrderStatus.PARTIALLY_AVAILABLE.name();
            }
        }
        order.setStatus(OrderStatus.UNPROCESSED.name());
        return OrderStatus.UNPROCESSED.name();
    }

    private boolean allRequestedProductsAreProcessed(Order order) {
        return order.getOrderProducts().stream()
                .filter(orderProduct -> orderProduct.getStatus().equals(UNKNOWN))
                .toList()
                .isEmpty();
    }

    private boolean allRequestedProductsReserved(Order order) {
        List<OrderProduct> reserved =
                order.getOrderProducts().stream()
                        .filter(orderProduct -> orderProduct.getStatus().equals(RESERVED))
                        .toList();
        return reserved.size() == order.getOrderProducts().size();
    }

    @Transactional
    public OrderResponse processUpdateOrder(Long orderId, OrderRequest orderRequest) {
        checkIfOrderExistsOrElseThrowException(orderId);
        Order order = orderMapper.toOrder(orderRequest);
        order.setOrderId(orderId);
        String status = orderRepository.findById(orderId).getStatus();
        order.setStatus(status);
        Order updatedOrder = updateOrder(order);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    private Order updateOrder(Order order) {
        orderRepository.getEntityManager().merge(order);
        return order;
    }

    private Order createOrderInDatabase(OrderRequest orderRequest) {
        Order order = orderMapper.toOrder(orderRequest);
        order.getOrderProducts().forEach(p -> p.setStatus(UNKNOWN));
        order.setStatus(OrderStatus.UNPROCESSED.name());
        orderRepository.persist(order);
        return order;
    }

    private void checkIfOrderExistsOrElseThrowException(Long id) {
        orderRepository
                .findByIdOptional(id)
                .orElseThrow(
                        () -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE.formatted(id)));
    }

    private Order getOrderOrElseThrowException(Long id) {
        return orderRepository
                .findByIdOptional(id)
                .orElseThrow(
                        () -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE.formatted(id)));
    }
}

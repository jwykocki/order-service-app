package com.jw.service;

import static com.jw.constants.OrderProductStatus.UNKNOWN;
import static com.jw.constants.OrderStatus.PROCESSED;
import static com.jw.constants.OrderStatus.UNPROCESSED;

import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import com.jw.error.OrderNotFoundException;
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
    public void deleteOrder(Long id) {
        checkIfOrderExistsOrElseThrowException(id);
        orderRepository.deleteById(id);
    }

    public OrderResponse getOrderById(Long id) {
        checkIfOrderExistsOrElseThrowException(id);
        Order order = orderRepository.findById(id);
        order.setStatus(updateOrderStatus(order.getOrderProducts()));
        return orderMapper.toOrderResponse(order);
    }

    private String updateOrderStatus(List<OrderProduct> orderProducts) {
        List<OrderProduct> orderProducts1 =
                orderProducts.stream()
                        .filter(orderProduct -> orderProduct.getStatus().equals("NOT KNOWN"))
                        .toList();
        if (orderProducts1.isEmpty()) {
            return PROCESSED;
        }
        return UNPROCESSED;
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
        order.setStatus(UNPROCESSED);
        orderRepository.persist(order);
        return order;
    }

    private void checkIfOrderExistsOrElseThrowException(Long id) {
        orderRepository
                .findByIdOptional(id)
                .orElseThrow(
                        () ->
                                new OrderNotFoundException(
                                        "Order with id = %s was not found".formatted(id)));
    }
}

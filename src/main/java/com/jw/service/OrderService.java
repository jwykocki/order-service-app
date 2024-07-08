package com.jw.service;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.entity.Order;
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

    @Transactional
    public Order saveOrder(Order order) {
        orderRepository.persist(order);
        return order;
    }

    public OrderResponse processOrder(OrderRequest orderRequest) {
        log.info("Processing order {}", orderRequest);
        Order order = orderMapper.toOrder(orderRequest);
        order.setStatus("UNCOMPLETED");
        saveOrder(order);
        log.info("Order {} processed", order);
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
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse processUpdateOrder(Long orderId, OrderRequest orderRequest) {
        checkIfOrderExistsOrElseThrowException(orderId);
        Order order = orderMapper.toOrder(orderRequest);
        order.setOrderId(orderId);
        String status = orderRepository.findById(orderId).getStatus();
        order.setStatus(status);
        updateOrder(order);
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public Order updateOrder(Order order) {
        orderRepository.getEntityManager().merge(order);
        return order;
    }

    private void checkIfOrderExistsOrElseThrowException(Long id) {
        orderRepository
                .findByIdOptional(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }
}

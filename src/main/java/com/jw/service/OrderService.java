package com.jw.service;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ValidatorService orderValidator;

    @Transactional
    public void saveOrder(OrderRequest orderRequest) {
        com.jw.entity.Order order = orderMapper.toOrder(orderRequest);
        orderValidator.validate(order);
        orderRepository.persist(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.listAll().stream().map(orderMapper::toOrderResponse).toList();
    }

    @Transactional
    public void deleteOrder(String id) {
        Long orderId = Long.valueOf(id);
        orderRepository.deleteById(orderId);
    }

    public OrderResponse getOrderById(String id) {
        Long orderId = Long.valueOf(id);
        Order order = orderRepository.findById(orderId);
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public void updateOrder(OrderRequest orderRequest) {
        orderRepository.getEntityManager().merge(orderMapper.toOrder(orderRequest));
    }
}

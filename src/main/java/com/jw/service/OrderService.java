package com.jw.service;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
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
        com.jw.entity.Order order = orderMapper.mapOrderRequestToOrder(orderRequest);
        orderValidator.validate(order);
        orderRepository.persist(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.listAll().stream()
                .map(orderMapper::mapOrderToOrderResponse)
                .toList();
    }
}

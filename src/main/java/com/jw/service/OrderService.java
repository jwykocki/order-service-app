package com.jw.service;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.entity.Order;
import com.jw.error.OrderNotFoundException;
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
    public void deleteOrder(Long id) {
        checkIfOrderExistsOrElseThrowException(id);
        orderRepository.deleteById(id);
    }

    public OrderResponse getOrderById(Long id) {
        checkIfOrderExistsOrElseThrowException(id);
        Order order = orderRepository.findById(id);
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public void updateOrder(OrderRequest orderRequest) {
        checkIfOrderExistsOrElseThrowException(orderRequest.id());
        orderRepository.getEntityManager().merge(orderMapper.toOrder(orderRequest));
    }

    private void checkIfOrderExistsOrElseThrowException(Long id) {
        orderRepository
                .findByIdOptional(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }
}

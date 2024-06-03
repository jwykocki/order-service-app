/* (C)2024 */
package com.jw.service;

import static com.jw.dto.OrderMapper.mapOrderRequestToOrder;

import com.jw.dto.OrderMapper;
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

    @Transactional
    public void saveOrder(OrderRequest orderRequest) {
        orderRepository.persist(mapOrderRequestToOrder(orderRequest));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.listAll().stream()
                .map(OrderMapper::mapOrdertoOrderResponse)
                .toList();
    }
}

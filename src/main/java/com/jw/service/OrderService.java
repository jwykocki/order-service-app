package com.jw.service;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.entity.OrderDao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    public void saveOrder(OrderRequest orderRequest) {
        OrderDao orderToSave = new OrderDao();
        orderToSave.setName(orderRequest.name());
        orderRepository.persist(orderToSave);
    }

    public List<OrderResponse> getAllorders() {
        List<OrderDao> ordersDao = orderRepository.listAll();
        return ordersDao.stream().map(orderDao -> new OrderResponse(orderDao.getId(), orderDao.getName())).toList();
    }
}

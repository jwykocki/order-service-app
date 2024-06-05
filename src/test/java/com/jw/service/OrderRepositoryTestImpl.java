package com.jw.service;

import com.jw.entity.Order;
import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
@Mock
public class OrderRepositoryTestImpl extends OrderRepository {

    Set<Order> orders;

    public OrderRepositoryTestImpl() {
        orders = new HashSet<>();
    }

    @Override
    public void persist(Order order) {
        orders.add(order);
    }

    @Override
    public List<Order> listAll() {
        List<Order> orderList = new ArrayList<>();
        orders.iterator().forEachRemaining(orderList::add);
        return orderList;
    }
}

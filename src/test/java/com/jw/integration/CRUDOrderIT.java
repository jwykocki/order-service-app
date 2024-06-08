package com.jw.integration;

import static com.jw.OrderTestFixtures.*;
import static com.jw.resources.RequestCaller.callEndpointAndAssertStatusCodeAndReturn;
import static org.assertj.core.api.Assertions.assertThat;

import com.jw.dto.OrderResponse;
import com.jw.dto.OrdersResponse;
import com.jw.entity.Order;
import com.jw.service.OrderRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.HttpMethod;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(value = CRUDOrderITConfiguration.class)
@RequiredArgsConstructor
public class CRUDOrderIT {

    private final OrderRepository orderRepository;

    @Transactional
    public void deleteRows() {
        orderRepository.deleteAll();
        assertThat(orderRepository.count()).isEqualTo(0);
    }

    @Transactional
    public void saveOrder(Order order) {
        orderRepository.persist(order);
    }

    @Transactional
    public void flushDb() {
        orderRepository.flush();
    }

    @BeforeEach
    @AfterEach
    public void cleanUp() {
        deleteRows();
    }

    @Test
    public void shouldSaveOrdersToDatabase() {

        // given
        assertThat(orderRepository.listAll().size()).isEqualTo(0);

        // when
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.POST, "/order", VALID_ORDER_REQUEST, 204);
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.POST, "/order", VALID_ORDER_REQUEST_2, 204);

        // then
        List<Order> orders = orderRepository.listAll();
        assertThat(orders.size()).isEqualTo(2);
        List<String> orderNames = orders.stream().map(Order::getName).toList();
        assertThat(orderNames)
                .containsExactlyInAnyOrder(TEST_ORDER_REQUEST_NAME, TEST_ORDER_REQUEST_NAME_2);
    }

    @Test
    public void shouldGetOrdersFromDatabase() {

        // given
        assertThat(orderRepository.listAll().size()).isEqualTo(0);
        Order order = new Order();
        Order order2 = new Order();
        order.setName(TEST_ORDER_REQUEST_NAME);
        order2.setName(TEST_ORDER_REQUEST_NAME_2);
        saveOrder(order);
        saveOrder(order2);
        assertThat(orderRepository.listAll().size()).isEqualTo(2);

        // when
        OrdersResponse ordersResponse =
                callEndpointAndAssertStatusCodeAndReturn(HttpMethod.GET, "/order", "", 200)
                        .as(OrdersResponse.class);

        // then
        List<OrderResponse> orders = ordersResponse.getOrders();
        assertThat(orders.size()).isEqualTo(2);
        List<String> orderNames = orders.stream().map(OrderResponse::name).toList();
        assertThat(orderNames)
                .containsExactlyInAnyOrder(TEST_ORDER_REQUEST_NAME, TEST_ORDER_REQUEST_NAME_2);
    }

    @Test
    public void shouldGetOrderById() {

        // given
        Order order = new Order();
        order.setName(TEST_ORDER_REQUEST_NAME);
        saveOrder(order);
        List<Order> orders = orderRepository.listAll();
        assertThat(orders.size()).isEqualTo(1);
        Long orderId = orders.get(0).getId();

        // when
        OrderResponse orderResponse =
                callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.GET, "/order/" + orderId, "", 200)
                        .as(OrderResponse.class);

        // then
        assertThat(orderResponse.id()).isEqualTo(orderId);
        assertThat(orderResponse.name()).isEqualTo(TEST_ORDER_REQUEST_NAME);
    }

    @Test
    public void shouldDeleteOrder() {

        // given
        Order order = new Order();
        order.setName(TEST_ORDER_REQUEST_NAME);
        saveOrder(order);
        List<Order> orders = orderRepository.listAll();
        assertThat(orders.size()).isEqualTo(1);
        Long orderId = orders.get(0).getId();

        // when
        callEndpointAndAssertStatusCodeAndReturn(HttpMethod.DELETE, "/order/" + orderId, "", 204);

        // then
        assertThat(orderRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldUpdateOrder() {

        // given
        Order order = new Order();
        order.setName(TEST_ORDER_REQUEST_NAME);
        saveOrder(order);
        List<Order> ordersBeforeUpdate = orderRepository.listAll();
        assertThat(ordersBeforeUpdate.size()).isEqualTo(1);
        Long orderId = ordersBeforeUpdate.get(0).getId();

        // when
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.PUT, "/order", generateOrderUpdateRequest(orderId), 204);

        // then
        orderRepository.getEntityManager().clear();
        List<Order> orders = orderRepository.listAll();
        assertThat(orders.size()).isEqualTo(1);
        assertThat(orders.get(0).getId()).isEqualTo(orderId);
        assertThat(orders.get(0).getName()).isEqualTo(TEST_ORDER_REQUEST_NAME_2);
    }
}

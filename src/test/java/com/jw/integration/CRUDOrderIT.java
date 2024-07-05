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
import org.apache.http.HttpStatus;
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
                HttpMethod.POST, "/order", VALID_ORDER_REQUEST, HttpStatus.SC_NO_CONTENT);
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.POST, "/order", VALID_ORDER_REQUEST_2, HttpStatus.SC_NO_CONTENT);

        // then
        List<Order> orders = orderRepository.listAll();
        assertThat(orders.size()).isEqualTo(2);
        List<Long> orderCustomerIds = orders.stream().map(Order::getCustomerId).toList();
        assertThat(orderCustomerIds)
                .containsExactlyInAnyOrder(
                        TEST_ORDER_REQUEST_CUSTOMER_ID, TEST_ORDER_REQUEST_CUSTOMER_ID_2);
    }

    @Test
    public void shouldGetOrdersFromDatabase() {

        // given
        assertThat(orderRepository.listAll().size()).isEqualTo(0);
        Order order = new Order();
        Order order2 = new Order();
        order.setCustomerId(TEST_ORDER_REQUEST_CUSTOMER_ID);
        order2.setCustomerId(TEST_ORDER_REQUEST_CUSTOMER_ID_2);
        saveOrder(order);
        saveOrder(order2);
        assertThat(orderRepository.listAll().size()).isEqualTo(2);

        // when
        OrdersResponse ordersResponse =
                callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.GET, "/order", "", HttpStatus.SC_OK)
                        .as(OrdersResponse.class);

        // then
        List<OrderResponse> orders = ordersResponse.getOrders();
        assertThat(orders.size()).isEqualTo(2);
        List<Long> orderCustomerIds = orders.stream().map(OrderResponse::customerId).toList();
        assertThat(orderCustomerIds)
                .containsExactlyInAnyOrder(
                        TEST_ORDER_REQUEST_CUSTOMER_ID, TEST_ORDER_REQUEST_CUSTOMER_ID_2);
    }

    @Test
    public void shouldGetOrderById() {

        // given
        Order order = new Order();
        order.setCustomerId(TEST_ORDER_REQUEST_CUSTOMER_ID);
        saveOrder(order);
        List<Order> orders = orderRepository.listAll();
        assertThat(orders.size()).isEqualTo(1);
        Long orderId = orders.get(0).getOrderId();

        // when
        OrderResponse orderResponse =
                callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.GET, "/order/" + orderId, "", HttpStatus.SC_OK)
                        .as(OrderResponse.class);

        // then
        assertThat(orderResponse.orderId()).isEqualTo(orderId);
        assertThat(orderResponse.customerId()).isEqualTo(TEST_ORDER_REQUEST_CUSTOMER_ID);
    }

    @Test
    public void shouldUpdateOrder() {

        // given
        Order order = new Order();
        order.setCustomerId(TEST_ORDER_REQUEST_CUSTOMER_ID);
        saveOrder(order);
        List<Order> ordersBeforeUpdate = orderRepository.listAll();
        assertThat(ordersBeforeUpdate.size()).isEqualTo(1);
        Long orderId = ordersBeforeUpdate.get(0).getOrderId();

        // when
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.PUT,
                "/order",
                generateOrderUpdateRequest(orderId),
                HttpStatus.SC_NO_CONTENT);

        // then
        orderRepository.getEntityManager().clear();
        List<Order> orders = orderRepository.listAll();
        assertThat(orders.size()).isEqualTo(1);
        assertThat(orders.get(0).getOrderId()).isEqualTo(orderId);
        assertThat(orders.get(0).getCustomerId()).isEqualTo(TEST_ORDER_REQUEST_CUSTOMER_ID_2);
    }

    @Test
    public void shouldDeleteOrder() {

        // given
        Order order = new Order();
        order.setCustomerId(123L);
        saveOrder(order);
        List<Order> orders = orderRepository.listAll();
        assertThat(orders.size()).isEqualTo(1);
        Long orderId = orders.get(0).getOrderId();

        // when
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.DELETE, "/order/" + orderId, "", HttpStatus.SC_NO_CONTENT);

        // then
        assertThat(orderRepository.count()).isEqualTo(0);
    }
}

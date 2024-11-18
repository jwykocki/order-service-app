package com.jw.integration;

import static com.jw.OrderTestFixtures.*;
import static com.jw.OrderTestFixtures.testOrderRequestWithTwoProducts;
import static com.jw.TestHelper.*;
import static com.jw.resources.RequestCaller.callEndpointAndAssertStatusCodeAndReturn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.entity.Order;
import com.jw.repository.OrderRepository;
import com.jw.service.QueueWriter;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.HttpMethod;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

@QuarkusTest
@QuarkusTestResource(value = ContainerITConfiguration.class)
@RequiredArgsConstructor
public class CRUDOrderIT {

    private final OrderRepository orderRepository;

    @InjectMock QueueWriter queueWriter;

    @Transactional
    public void deleteRows() {
        orderRepository.deleteAll();
        assertThat(orderRepository.listAll()).isEmpty();
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
        OrderRequest orderRequest1 = testOrderRequestWithTwoProducts();
        OrderRequest orderRequest2 = testOrderRequestWithOneProduct();

        doNothing().when(queueWriter).saveProductOnUnprocessedProducts(any());
        doNothing().when(queueWriter).saveOrderOnUnprocessedOrders(any());

        // when
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.POST, ORDER_ENDPOINT, asString(orderRequest1), HttpStatus.SC_OK);
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.POST, ORDER_ENDPOINT, asString(orderRequest2), HttpStatus.SC_OK);

        // then
        List<Order> orders = orderRepository.listAll();
        verify(queueWriter, times(orders.size())).saveOrderOnUnprocessedOrders(any());
        assertThat(orders).hasSize(2);
        assertCorrectOrdersRequest(
                orders,
                List.of(testOrderRequestWithTwoProducts(), testOrderRequestWithOneProduct()));
    }

    @Test
    public void shouldGetOrdersFromDatabase() {

        // given
        Order order1 = fromOrderRequest(testOrderRequestWithTwoProducts());
        Order order2 = fromOrderRequest(testOrderRequestWithOneProduct());
        saveOrder(order1);
        saveOrder(order2);
        assertThat(orderRepository.listAll()).hasSize(2);

        // when
        List<OrderResponse> orders =
                callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.GET, ORDER_ENDPOINT, StringUtils.EMPTY, HttpStatus.SC_OK)
                        .as(new TypeRef<>() {});

        // then
        assertThat(orders).hasSize(2);
        assertCorrectOrdersResponse(List.of(order1, order2), orders);
    }

    @Test
    public void shouldGetOrderById() {

        // given
        Order order = fromOrderRequest(testOrderRequestWithTwoProducts());
        saveOrder(order);
        List<Order> orders = orderRepository.listAll();
        assertThat(orders).hasSize(1);
        Long orderId = orders.get(0).getOrderId();

        // when
        OrderResponse orderResponse =
                callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.GET,
                                ORDER_ENDPOINT + "/" + orderId,
                                StringUtils.EMPTY,
                                HttpStatus.SC_OK)
                        .as(OrderResponse.class);

        // then
        assertThat(orderResponse.orderId()).isEqualTo(orderId);
        assertCorrectOrdersResponse(List.of(order), List.of(orderResponse));
    }

    @Test
    public void shouldUpdateOrder() {

        // given
        Order orderBeforeUpdate = fromOrderRequest(testOrderRequestWithTwoProducts());
        saveOrder(orderBeforeUpdate);
        List<Order> ordersBeforeUpdate = orderRepository.listAll();
        assertThat(ordersBeforeUpdate).hasSize(1);
        Long orderId = ordersBeforeUpdate.get(0).getOrderId();

        // when
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.PUT,
                ORDER_ENDPOINT + "/" + orderId,
                asString(testOrderRequestWithOneProduct()),
                HttpStatus.SC_OK);

        // then
        orderRepository.getEntityManager().clear();
        List<Order> ordersAfterUpdate = orderRepository.listAll();
        assertThat(ordersAfterUpdate).hasSize(1);
        Order orderAfterUpdate = ordersAfterUpdate.get(0);
        assertThat(orderAfterUpdate.getOrderId()).isEqualTo(orderId);
        assertCorrectOrdersContent(
                List.of(orderAfterUpdate),
                List.of(fromOrderRequest(testOrderRequestWithOneProduct())));
    }

    @Test
    public void shouldDeleteOrder() {

        // given
        Order order = fromOrderRequest(testOrderRequestWithTwoProducts());
        saveOrder(order);
        List<Order> orders = orderRepository.listAll();
        assertThat(orders).hasSize(1);
        Long orderId = orders.get(0).getOrderId();

        // when
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.DELETE,
                ORDER_ENDPOINT + "/" + orderId,
                StringUtils.EMPTY,
                HttpStatus.SC_OK);

        // then
        assertThat(orderRepository.listAll()).isEmpty();
    }
}

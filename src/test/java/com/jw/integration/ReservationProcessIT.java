package com.jw.integration;

import static com.jw.OrderTestFixtures.*;
import static com.jw.TestHelper.asString;
import static com.jw.constants.OrderProductStatus.*;
import static com.jw.constants.OrderStatus.*;
import static com.jw.resources.RequestCaller.callEndpointAndAssertStatusCodeAndReturn;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jw.constants.OrderStatus;
import com.jw.dto.finalize.request.FinalizedOrderQueue;
import com.jw.dto.finalize.request.OrderFinalizeRequest;
import com.jw.dto.finalize.request.OrderFinalizeResponse;
import com.jw.dto.finalize.request.OrderProductFinalizeRequest;
import com.jw.dto.processed.ProductReservationResult;
import com.jw.dto.request.OrderProductRequest;
import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.dto.unprocessed.orders.OrderProductQueue;
import com.jw.dto.unprocessed.orders.UnprocessedOrderQueue;
import com.jw.dto.unprocessed.products.UnprocessedProductQueue;
import com.jw.entity.Order;
import com.jw.service.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import jakarta.ws.rs.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.awaitility.Awaitility;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
@QuarkusTestResource(value = ContainerITConfiguration.class)
public class ReservationProcessIT {

    @Inject OrderProductMapper orderProductMapper;

    @Inject DatabaseQueryExecutor dbExecutor;

    @Inject QueueHelper queueTestHelper;

    @InjectSpy QueueReader queueReader;

    @InjectSpy OrderService orderService;

    @InjectSpy QueueWriter queueWriter;

    @Captor ArgumentCaptor<UnprocessedOrderQueue> unprocessedOrderQueueCaptor;

    @Captor ArgumentCaptor<byte[]> productReservationResultCaptor;

    @Captor ArgumentCaptor<UnprocessedProductQueue> unprocessedProductQueueCaptor;

    @Captor ArgumentCaptor<FinalizedOrderQueue> finalizedOrderQueueCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void deleteOrder() {
        dbExecutor.deleteOrder(TEST_CUSTOMER_ID_1);
    }

    @Test
    public void shouldSaveOrderToDbAndSendRequestsOnQueues() {

        // given
        assertNoRowsWithCustomerId(TEST_CUSTOMER_ID_1);

        OrderRequest orderRequest1 =
                new OrderRequest(
                        TEST_CUSTOMER_ID_1,
                        List.of(new OrderProductRequest(1L, 2), new OrderProductRequest(2L, 4)));

        // when
        OrderResponse orderResponse =
                callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.POST,
                                ORDER_ENDPOINT,
                                asString(orderRequest1),
                                HttpStatus.SC_OK)
                        .as(OrderResponse.class);

        // then
        Long orderId = orderResponse.orderId();
        assertThat(orderId).isNotNull();
        assertProperOrderResponse(
                orderResponse,
                OrderStatus.UNPROCESSED.name(),
                testProductsWithStatuses(UNKNOWN, UNKNOWN));

        Order order = assertOneOrderInDatabaseAndReturn(TEST_CUSTOMER_ID_1);
        assertProperOrder(
                order, OrderStatus.UNPROCESSED.name(), testProductsWithStatuses(UNKNOWN, UNKNOWN));

        verify(queueWriter, times(1))
                .saveOrderOnUnprocessedOrders(any(UnprocessedOrderQueue.class));

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            verify(queueReader, times(1))
                                    .readUnprocessedOrders(unprocessedOrderQueueCaptor.capture());
                            UnprocessedOrderQueue value = unprocessedOrderQueueCaptor.getValue();
                            assertThat(value.orderId()).isEqualTo(orderId);
                        });

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            verify(queueWriter, times(2))
                                    .saveProductOnUnprocessedProducts(
                                            unprocessedProductQueueCaptor.capture());
                            UnprocessedProductQueue value =
                                    unprocessedProductQueueCaptor.getValue();
                            assertThat(value.orderId()).isEqualTo(orderId);
                        });
    }

    @Test
    public void shouldReadProcessedProductsFromQueuesAndSendToFinalizedProducts() {

        // given
        assertNoRowsWithCustomerId(TEST_CUSTOMER_ID_1);
        saveTestOrderToDatabase(TEST_CUSTOMER_ID_1);
        assertOneProperOrderInDatabase(TEST_CUSTOMER_ID_1);

        // when
        queueTestHelper.sentToProcessedProducts(
                new ProductReservationResult(
                        TEST_ORDER_ID, new OrderProductQueue(1L, 2), RESERVED));
        queueTestHelper.sentToProcessedProducts(
                new ProductReservationResult(
                        TEST_ORDER_ID, new OrderProductQueue(2L, 4), RESERVED));

        // then
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            verify(queueReader, times(2))
                                    .readProcessedProducts(
                                            productReservationResultCaptor.capture());
                            List<byte[]> allValues = productReservationResultCaptor.getAllValues();
                            allValues.stream()
                                    .map(b -> new String(b, StandardCharsets.UTF_8))
                                    .map(s -> orderProductMapper.toProductReservationResult(s))
                                    .forEach(p -> assertThat(p.status()).isEqualTo(RESERVED));
                        });

        verify(orderService, times(2)).updateOrderStatusAndReturn(TEST_ORDER_ID);

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            verify(queueWriter, times(2))
                                    .saveProductOnFinalizedProducts(
                                            finalizedOrderQueueCaptor.capture());
                            List<FinalizedOrderQueue> allValues =
                                    finalizedOrderQueueCaptor.getAllValues();
                            allValues.forEach(
                                    p -> assertThat(p.orderId()).isEqualTo(TEST_ORDER_ID));
                        });

        Order order = assertOneOrderInDatabaseAndReturn(TEST_CUSTOMER_ID_1);
        assertProperOrder(
                order,
                OrderStatus.FINALIZED.name(),
                testProductsWithStatuses(
                        OrderStatus.FINALIZED.name(), OrderStatus.FINALIZED.name()));
    }

    @Test
    void shouldFinalizePartiallyAvailableOrder() {

        // given
        assertNoRowsWithCustomerId(TEST_CUSTOMER_ID_1);
        saveTestOrderToDatabase(TEST_CUSTOMER_ID_1);
        assertOneProperOrderInDatabase(TEST_CUSTOMER_ID_1);

        queueTestHelper.sentToProcessedProducts(
                new ProductReservationResult(
                        TEST_ORDER_ID, new OrderProductQueue(1L, 2), RESERVED));
        queueTestHelper.sentToProcessedProducts(
                new ProductReservationResult(
                        TEST_ORDER_ID, new OrderProductQueue(2L, 4), NOT_AVAILABLE));

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            verify(queueReader, times(2))
                                    .readProcessedProducts(
                                            productReservationResultCaptor.capture());
                            List<byte[]> allValues = productReservationResultCaptor.getAllValues();
                            allValues.stream()
                                    .map(b -> new String(b, StandardCharsets.UTF_8))
                                    .map(s -> orderProductMapper.toProductReservationResult(s))
                                    .forEach(p -> assertThat(p.orderId()).isEqualTo(TEST_ORDER_ID));
                        });

        Order order = assertOneOrderInDatabaseAndReturn(TEST_CUSTOMER_ID_1);
        assertProperOrder(
                order,
                OrderStatus.PARTIALLY_AVAILABLE.name(),
                testProductsWithStatuses(RESERVED, NOT_AVAILABLE));

        OrderFinalizeRequest orderFinalizeRequest =
                new OrderFinalizeRequest(
                        TEST_ORDER_ID,
                        TEST_CUSTOMER_ID_1,
                        List.of(new OrderProductFinalizeRequest(1L, 2)));
        OrderFinalizeResponse finalizeResponse =
                callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.POST,
                                FINALIZE_ENDPOINT,
                                asString(orderFinalizeRequest),
                                HttpStatus.SC_OK)
                        .as(OrderFinalizeResponse.class);
        assertOneProductWasFinalized(finalizeResponse, 1L, 2);

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            verify(queueWriter, times(1))
                                    .saveProductOnFinalizedProducts(
                                            finalizedOrderQueueCaptor.capture());
                            FinalizedOrderQueue value = finalizedOrderQueueCaptor.getValue();
                            assertThat(value.orderId()).isEqualTo(TEST_ORDER_ID);
                            assertThat(value.product().productId()).isEqualTo(1);
                            assertThat(value.product().finalized()).isEqualTo(2);
                        });

        Order order2 = assertOneOrderInDatabaseAndReturn(TEST_CUSTOMER_ID_1);
        assertProperOrder(
                order2,
                FINALIZED.name(),
                testProductsWithStatuses(OrderStatus.FINALIZED.name(), NOT_AVAILABLE));
    }

    private void assertNoRowsWithCustomerId(Long customerId) {
        assertThat(selectOrdersWithCustomerId(customerId).size()).isEqualTo(0);
    }

    private Order assertOneOrderInDatabaseAndReturn(Long customerId) {
        List<Order> orders1 = selectOrdersWithCustomerId(customerId);
        assertThat(orders1.size()).isEqualTo(1);
        return orders1.get(0);
    }

    private List<Order> selectOrdersWithCustomerId(Long customerId) {
        return dbExecutor.returnRowsWithCustomerId(customerId);
    }

    private void saveTestOrderToDatabase(Long customerId) {
        dbExecutor.saveOrder(
                new OrderRequest(
                        customerId,
                        List.of(new OrderProductRequest(1L, 2), new OrderProductRequest(2L, 4))));
    }

    private void assertOneProperOrderInDatabase(Long customerId) {
        Order order = assertOneOrderInDatabaseAndReturn(customerId);
        assertProperOrder(
                order, OrderStatus.UNPROCESSED.name(), testProductsWithStatuses(UNKNOWN, UNKNOWN));
    }
}

package com.jw;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.jw.constants.OrderProductStatus;
import com.jw.constants.OrderStatus;
import com.jw.dto.processed.ProductReservationResponse;
import com.jw.dto.request.OrderProductRequest;
import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import java.util.List;
import lombok.SneakyThrows;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class TestHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void assertCorrectOrdersContent(List<Order> orders, List<Order> orders2) {
        assertThat(orders).hasSize(orders2.size());
        List<List<OrderProduct>> orderProducts1 =
                orders.stream().map(Order::getOrderProducts).toList();
        List<List<OrderProduct>> orderProducts2 =
                orders2.stream().map(Order::getOrderProducts).toList();
        assertThat(orders)
                .usingRecursiveFieldByFieldElementComparatorOnFields("customerId")
                .isEqualTo(orders2);
        assertThat(orderProducts1)
                .usingRecursiveFieldByFieldElementComparatorOnFields("customerId", "quantity")
                .isEqualTo(orderProducts2);
    }

    public static void assertCorrectOrdersRequest(
            List<Order> orders, List<OrderRequest> orderRequests) {
        List<Order> orders2 = orderRequests.stream().map(TestHelper::fromOrderRequest).toList();
        assertCorrectOrdersContent(orders, orders2);
    }

    public static void assertCorrectOrdersResponse(
            List<Order> orders, List<OrderResponse> orderResponses) {
        List<Order> orders2 = orderResponses.stream().map(TestHelper::fromOrderResponse).toList();
        assertCorrectOrdersContent(orders, orders2);
    }

    public static Order fromOrderRequest(OrderRequest orderRequest) {
        List<OrderProduct> orderProducts =
                orderRequest.orderProducts().stream().map(TestHelper::createOrderProduct).toList();
        return Order.builder()
                .status(OrderStatus.UNPROCESSED)
                .customerId(orderRequest.customerId())
                .orderProducts(orderProducts)
                .build();
    }

    private static Order fromOrderResponse(OrderResponse orderResponse) {
        List<OrderProduct> orderProducts =
                orderResponse.orderProducts().stream().map(TestHelper::createOrderProduct).toList();
        return Order.builder()
                .orderId(orderResponse.orderId())
                .status(orderResponse.status())
                .customerId(orderResponse.customerId())
                .orderProducts(orderProducts)
                .build();
    }

    private static OrderProduct createOrderProduct(
            ProductReservationResponse productReservationResponse) {
        return OrderProduct.builder()
                .productId(productReservationResponse.productId())
                .quantity(productReservationResponse.quantity())
                .build();
    }

    private static OrderProduct createOrderProduct(OrderProductRequest orderProductRequest) {
        return OrderProduct.builder()
                .status(OrderProductStatus.RESERVED)
                .productId(orderProductRequest.productId())
                .quantity(orderProductRequest.quantity())
                .build();
    }

    @SneakyThrows
    public static String asString(Object object) {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper.writeValueAsString(object);
    }
}

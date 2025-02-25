package com.jw;

import static com.jw.resources.ProductComparisonEntity.from;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.jw.constants.OrderProductStatus;
import com.jw.constants.OrderStatus;
import com.jw.dto.finalize.request.OrderFinalizeResponse;
import com.jw.dto.processed.ProductReservationResponse;
import com.jw.dto.request.OrderProductRequest;
import com.jw.dto.request.OrderRequest;
import com.jw.dto.response.OrderResponse;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import com.jw.resources.ProductComparisonEntity;
import java.util.List;
import org.assertj.core.api.Assertions;

public class OrderTestFixtures {

    public static final Long TEST_ORDER_ID = 9999L;
    public static final Long TEST_CUSTOMER_ID_1 = 123L;
    public static final Long TEST_CUSTOMER_ID_2 = 456L;
    public static final Long TEST_PRODUCT_1_ID = 1L;
    public static final Long TEST_PRODUCT_2_ID = 2L;
    public static final int TEST_PRODUCT_1_QUANTITY = 3;
    public static final int TEST_PRODUCT_2_QUANTITY = 4;
    public static final String ORDER_ENDPOINT = "/order";
    public static final String FINALIZE_ENDPOINT = "order/finalize";

    public static final OrderProductRequest TEST_PRODUCT_1 =
            new OrderProductRequest(TEST_PRODUCT_1_ID, TEST_PRODUCT_1_QUANTITY);
    public static final OrderProductRequest TEST_PRODUCT_2 =
            new OrderProductRequest(TEST_PRODUCT_2_ID, TEST_PRODUCT_2_QUANTITY);

    public static OrderRequest testOrderRequestWithTwoProducts() {
        return new OrderRequest(TEST_CUSTOMER_ID_1, List.of(TEST_PRODUCT_1, TEST_PRODUCT_2));
    }

    public static OrderRequest testOrderRequestWithOneProduct() {
        return new OrderRequest(TEST_CUSTOMER_ID_2, List.of(TEST_PRODUCT_1));
    }

    public static void assertProperOrder(
            Order order, OrderStatus orderStatus, List<ProductComparisonEntity> expectedProducts) {
        assertThat(order.getStatus()).isEqualTo(orderStatus);
        assertThat(order.getCustomerId()).isEqualTo(TEST_CUSTOMER_ID_1);
        assertThat(order.getOrderProducts().size()).isEqualTo(expectedProducts.size());
        Assertions.assertThat(toProductComparisonEntities(order.getOrderProducts()))
                .usingRecursiveFieldByFieldElementComparatorOnFields(
                        "productId", "quantity", "status")
                .containsExactlyInAnyOrderElementsOf(expectedProducts);
    }

    public static void assertProperOrderResponse(
            OrderResponse orderResponse,
            OrderStatus orderStatus,
            List<ProductComparisonEntity> expectedProducts) {
        assertProperOrder(toOrder(orderResponse), orderStatus, expectedProducts);
    }

    private static Order toOrder(OrderResponse orderResponse) {
        Order order = new Order();
        order.setOrderId(orderResponse.orderId());
        order.setCustomerId(orderResponse.customerId());
        order.setStatus(orderResponse.status());
        order.setOrderProducts(
                orderResponse.orderProducts().stream()
                        .map(OrderTestFixtures::toOrderProduct)
                        .toList());
        return order;
    }

    private static OrderProduct toOrderProduct(ProductReservationResponse p) {
        return new OrderProduct(null, null, p.productId(), p.quantity(), p.status());
    }

    private static List<ProductComparisonEntity> toProductComparisonEntities(
            List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(
                        p ->
                                ProductComparisonEntity.from(
                                        p.getProductId(), p.getQuantity(), p.getStatus()))
                .toList();
    }

    private static List<ProductComparisonEntity> mapProductResponsesToComparisonEntities(
            List<ProductReservationResponse> orderProducts) {
        return orderProducts.stream().map(ProductComparisonEntity::from).toList();
    }

    public static List<ProductComparisonEntity> testProductsWithStatuses(
            OrderProductStatus status1, OrderProductStatus status2) {
        return List.of(from(1L, 2, status1), from(2L, 4, status2));
    }

    public static void assertOneProductWasFinalized(
            OrderFinalizeResponse finalizeResponse, Long productId, int finalized) {
        assertThat(finalizeResponse.orderId()).isEqualTo(TEST_ORDER_ID);
        assertThat(finalizeResponse.finalizedProducts().size()).isEqualTo(1);
        assertThat(finalizeResponse.finalizedProducts().get(0).productId()).isEqualTo(productId);
        assertThat(finalizeResponse.finalizedProducts().get(0).finalized()).isEqualTo(finalized);
    }
}

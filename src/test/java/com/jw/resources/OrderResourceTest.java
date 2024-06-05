package com.jw.resources;

import static com.jw.OrderTestFixtures.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.jw.dto.OrderResponse;
import com.jw.dto.OrdersResponse;
import com.jw.entity.Order;
import com.jw.error.ErrorResponse;
import com.jw.service.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@QuarkusTest
class OrderResourceTest {

    @Inject OrderRepositoryTestImpl orderRepositoryTest;
    @Inject OrderMapper orderMapper;

    @Test
    void shouldReturn204NoContent() {

        given().when()
                .contentType("application/json")
                .body(VALID_ORDER_REQUEST)
                .post("/order")
                .then()
                .statusCode(204);
    }

    @ParameterizedTest
    @EnumSource(InvalidRequest.class)
    void shouldReturn400BadRequestAndTheProperMessages(InvalidRequest invalidRequest) {

        ErrorResponse errorResponse =
                given().when()
                        .contentType("application/json")
                        .body(invalidRequest.body)
                        .post("/order")
                        .then()
                        .statusCode(400)
                        .extract()
                        .response()
                        .body()
                        .as(ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Request body is not valid");
        assertThat(errorResponse.getErrors()).hasSameElementsAs(invalidRequest.expectedMessages);
    }

    @Test
    void shouldReturnListOfOrders() {

        Order order1 = new Order(1, "test1");
        Order order2 = new Order(2, "test2");
        orderRepositoryTest.persist(order1);
        orderRepositoryTest.persist(order2);
        OrderResponse orderResponse1 = orderMapper.mapOrderToOrderResponse(order1);
        OrderResponse orderResponse2 = orderMapper.mapOrderToOrderResponse(order2);

        OrdersResponse receivedResponse =
                given().when()
                        .contentType("application/json")
                        .get("/order")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(OrdersResponse.class);

        assertThat(receivedResponse.getOrders())
                .containsExactlyInAnyOrder(orderResponse1, orderResponse2);
    }

    @RequiredArgsConstructor
    enum InvalidRequest {
        WITHOUT_FIELD(
                BODY_WITHOUT_REQUIRED_FIELD,
                List.of(
                        "order name must not be empty",
                        "order name must not be blank",
                        "order name must not be null")),
        WITH_EMPTY_FIELD(
                BODY_WITH_EMPTY_FIELD,
                List.of("order name must not be empty", "order name must not be blank")),
        WITH_BLANK_FIELD(BODY_WITH_BLANK_FIELD, List.of("order name must not be blank"));

        private final String body;
        private final List<String> expectedMessages;
    }
}

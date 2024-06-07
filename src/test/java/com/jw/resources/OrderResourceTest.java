package com.jw.resources;

import static com.jw.OrderTestFixtures.*;
import static com.jw.resources.RequestCaller.callEndpointAndAssertStatusCode;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.jw.dto.OrderResponse;
import com.jw.dto.OrdersResponse;
import com.jw.entity.Order;
import com.jw.error.ErrorResponse;
import com.jw.service.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@QuarkusTest
class OrderResourceTest {

    @InjectMock OrderRepository orderRepository;

    @Test
    void shouldReturn204NoContent() {
        callEndpointAndAssertStatusCode("/order", VALID_ORDER_REQUEST, 204);
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

        OrderResponse orderResponse1 = new OrderResponse(1, "test1");
        OrderResponse orderResponse2 = new OrderResponse(2, "test2");

        Order order1 = new Order(1, "test1");
        Order order2 = new Order(2, "test2");

        MockitoAnnotations.initMocks(this);
        Mockito.when(orderRepository.listAll()).thenReturn(List.of(order1, order2));

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
        WITHOUT_FIELD(BODY_WITHOUT_REQUIRED_FIELD, List.of("order name must be populated")),
        WITH_EMPTY_FIELD(BODY_WITH_EMPTY_FIELD, List.of("order name must be populated")),
        WITH_BLANK_FIELD(BODY_WITH_BLANK_FIELD, List.of("order name must be populated"));

        private final String body;
        private final List<String> expectedMessages;
    }
}

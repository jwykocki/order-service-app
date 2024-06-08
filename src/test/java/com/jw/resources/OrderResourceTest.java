package com.jw.resources;

import static com.jw.OrderTestFixtures.*;
import static com.jw.resources.RequestCaller.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jw.dto.OrderResponse;
import com.jw.dto.OrdersResponse;
import com.jw.entity.Order;
import com.jw.error.ErrorResponse;
import com.jw.service.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.HttpMethod;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@QuarkusTest
class OrderResourceTest {

    @InjectMock OrderRepository orderRepository;

    @Test
    void shouldReturn204NoContent() {
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.POST, "/order", VALID_ORDER_REQUEST, 204);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {BODY_WITHOUT_REQUIRED_FIELD, BODY_WITH_EMPTY_FIELD, BODY_WITH_BLANK_FIELD})
    void shouldReturn400BadRequestAndTheProperMessages(String invalidRequestBody) {

        ErrorResponse errorResponse =
                RequestCaller.callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.POST, "/order", invalidRequestBody, 400)
                        .as(ErrorResponse.class);

        assertThat(errorResponse.getMessage()).isEqualTo("Request body is not valid");
        assertThat(errorResponse.getErrors())
                .hasSameElementsAs(List.of("order name must be populated"));
    }

    @Test
    void shouldReturnListOfOrders() {

        OrderResponse orderResponse1 = new OrderResponse(1L, "test1");
        OrderResponse orderResponse2 = new OrderResponse(2L, "test2");

        Order order1 = new Order(1L, "test1");
        Order order2 = new Order(2L, "test2");

        MockitoAnnotations.initMocks(this);
        Mockito.when(orderRepository.listAll()).thenReturn(List.of(order1, order2));

        OrdersResponse receivedResponse =
                callEndpointAndAssertStatusCodeAndReturn(HttpMethod.GET, "/order", "", 200)
                        .as(OrdersResponse.class);

        assertThat(receivedResponse.getOrders())
                .containsExactlyInAnyOrder(orderResponse1, orderResponse2);
    }
}

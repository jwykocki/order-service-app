package com.jw.resources;

import static com.jw.OrderTestFixtures.*;
import static com.jw.resources.RequestCaller.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.dto.OrdersResponse;
import com.jw.dto.OrderProductRequest;
import com.jw.entity.Order;
import com.jw.error.ErrorResponse;
import com.jw.service.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.HttpMethod;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

@QuarkusTest
class OrderResourceTest {

    @InjectMock OrderRepository orderRepository;

    @Test
    void shouldReturn204NoContent() throws JsonProcessingException {

        // given && when && then

        OrderRequest orderRequest =  new OrderRequest(1L, List.of(new OrderProductRequest(1L, 3)));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String body = mapper.writeValueAsString(orderRequest);
        callEndpointAndAssertStatusCodeAndReturn(
                HttpMethod.POST, "/order", body, HttpStatus.SC_NO_CONTENT);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {BODY_WITHOUT_REQUIRED_FIELD, BODY_WITH_EMPTY_FIELD, BODY_WITH_BLANK_FIELD})
    void shouldReturn400BadRequestAndTheProperMessages(String invalidRequestBody) {

        // given && when
        ErrorResponse errorResponse =
                RequestCaller.callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.POST,
                                "/order",
                                invalidRequestBody,
                                HttpStatus.SC_BAD_REQUEST)
                        .as(ErrorResponse.class);

        // then
        assertThat(errorResponse.getMessage()).isEqualTo("Request body is not valid");
        assertThat(errorResponse.getErrors())
                .hasSameElementsAs(List.of("order name must be populated"));
    }

    @Test
    void shouldReturnListOfOrders() {

        OrderResponse orderResponse1 = new OrderResponse(1L, 3L);
        OrderResponse orderResponse2 = new OrderResponse(2L, 4L);

        Order order1 = new Order(1L, 3L);
        Order order2 = new Order(2L, 4L);

        Mockito.when(orderRepository.listAll()).thenReturn(List.of(order1, order2));

        OrdersResponse receivedResponse =
                callEndpointAndAssertStatusCodeAndReturn(
                                HttpMethod.GET, "/order", StringUtils.EMPTY, HttpStatus.SC_OK)
                        .as(OrdersResponse.class);

        assertThat(receivedResponse.getOrders())
                .containsExactlyInAnyOrder(orderResponse1, orderResponse2);
    }
}

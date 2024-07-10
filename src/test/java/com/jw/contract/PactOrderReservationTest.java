package com.jw.contract;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.jw.dto.OrderProductRequest;
import com.jw.dto.OrderResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "productService", hostInterface = "localhost", port = "8082")
public class PactOrderReservationTest {

    @Pact(consumer = "orderService")
    public V4Pact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder.given("test POST")
                .uponReceiving("POST REQUEST")
                .path("/reserve")
                .method("POST")
                .headers(headers)
                .body(
                        """
                        {
                            "customerId": 10,
                            "orderProducts": [
                                {
                                    "productId": 1,
                                    "quantity": 1
                                },
                                {
                                    "productId": 2,
                                    "quantity": 2
                                }
                            ]
                        }
                        """)
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(
                        """
                        {
                            "orderId": 1,
                            "status": "RESERVED",
                            "orderProducts": [
                                {
                                    "productId": 1,
                                    "quantity": 1
                                },
                                {
                                    "productId": 2,
                                    "quantity": 2
                                }
                            ]
                        }
                        """)
                .toPact(V4Pact.class);
    }

    @Test
    public void shouldReturn200WithProperHeaderAndBody() {
        // when
        Response response =
                RestAssured.given()
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .body(
                                """
                        {
                            "customerId": 10,
                            "orderProducts": [
                                {
                                    "productId": 1,
                                    "quantity": 1
                                },
                                {
                                    "productId": 2,
                                    "quantity": 2
                                }
                            ]
                        }
                        """)
                        .post("http://localhost:8082/reserve");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(response.getBody().as(OrderResponse.class).orderId()).isNotNull();
        assertThat(response.getBody().as(OrderResponse.class).status()).isNotNull();
        List<OrderProductRequest> products =
                response.getBody().as(OrderResponse.class).orderProducts();
        assertThat(products).hasSize(2);
        assertThat(products)
                .containsExactlyInAnyOrder(
                        new OrderProductRequest(1L, 1), new OrderProductRequest(2L, 2));
    }
}

package com.jw.contract;

import static io.restassured.RestAssured.given;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "productService", hostInterface = "localhost", port = "8082")
public class PactOrderReservationTest {

    String STRING_REQUEST =
            """
                    {
                        "orderId": 10,
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
                    """;

    @Pact(consumer = "orderService")
    public V4Pact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder.given("test POST")
                .uponReceiving("POST REQUEST")
                .path("/reserve")
                .method("POST")
                .headers(headers)
                .body(STRING_REQUEST)
                .willRespondWith()
                .headers(headers)
                .status(HttpStatus.SC_OK)
                .body(
                        """
                    {
                        "orderId": 10,
                        "status": "SUCCESS",
                        "message": "Reservation was processed successfully"
                    }
                    """)
                .toPact(V4Pact.class);
    }

    @Test
    public void shouldReturn200WithProperHeaderAndBody() {

        given().contentType("application/json")
                .body(STRING_REQUEST)
                .post("http://localhost:8082/reserve");
    }
    //    @Test
    //    public void shouldReturn200WithProperHeaderAndBody() {
    //        // when
    //        ResponseBody<Response> response =
    //                given()
    //                        .contentType("application/json")
    //                        .body(STRING_REQUEST)
    //                        .post("http://localhost:8082/reserve");
    //
    //        // then
    ////        assertThat(response.).isEqualTo(HttpStatus.SC_OK);
    //        assertThat(response.as(ReservationResult.class).orderId()).isEqualTo(10);
    //        assertThat(response.as(ReservationResult.class).status()).isEqualTo("RESERVED");
    //        assertThat(response.as(ReservationResult.class).message()).isEqualTo("Reservation was
    // processed successfully");
    //
    //    }
}

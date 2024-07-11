package com.jw.contract;

import static io.restassured.RestAssured.given;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "productService", hostInterface = "localhost", port = "8082")
class PactOrderReservationTest {

    public static final String STRING_REQUEST =
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
    public RequestResponsePact testReserve(PactDslWithProvider builder) {
        return builder.given("test POST")
                .uponReceiving("POST request")
                .path("/reserve")
                .method("POST")
                .body(STRING_REQUEST)
                .willRespondWith()
                .status(200)
                .body(
                        """
                {
                    "orderId": 10,
                    "status": "SUCCESS",
                    "message": "Reservation was processed successfully"
                }
                """)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "testReserve", pactVersion = PactSpecVersion.V3)
    void testReserve() {

        given().contentType("application/json")
                .body(STRING_REQUEST)
                .post("http://localhost:8082/reserve");
    }
}

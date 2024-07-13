package com.jw.contract;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

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

    private static final String CONTRACT_CONSUMER_REQUEST =
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

    private static final String CONTRACT_PROVIDER_RESPONSE =
            """
                {
                    "orderId": 10,
                    "status": "SUCCESS",
                    "message": "Reservation was processed successfully"
                }
            """;

    @Pact(consumer = "orderService")
    public RequestResponsePact testReserve(PactDslWithProvider builder) {
        return builder.given("test POST")
                .uponReceiving("POST request")
                .path("/reserve")
                .method("POST")
                .body(CONTRACT_CONSUMER_REQUEST)
                .willRespondWith()
                .status(200)
                .body(CONTRACT_PROVIDER_RESPONSE)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "testReserve", pactVersion = PactSpecVersion.V3)
    void testReserve() {

        given().when()
                .contentType("application/json")
                .body(CONTRACT_CONSUMER_REQUEST)
                .post("http://localhost:8082/reserve")
                .then()
                .log()
                .all()
                .statusCode(200)
                .body(is(CONTRACT_PROVIDER_RESPONSE));
    }
}

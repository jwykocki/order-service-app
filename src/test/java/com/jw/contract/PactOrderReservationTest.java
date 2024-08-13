package com.jw.contract;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.dto.reservation.ProductReservationRequest;
import com.jw.dto.reservation.ReservationResult;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import org.apache.http.HttpStatus;
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

    private static final Map<String, String> CONTRACT_REQUEST_HEADERS =
            Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    private static final Map<String, String> CONTRACT_RESPONSE_HEADERS =
            Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    private static final String RESERVE_ENDPOINT = "/reserve";
    private static final String PROVIDER_URL = "http://localhost:8082" + RESERVE_ENDPOINT;

    @Pact(consumer = "orderService")
    public RequestResponsePact testReserve(PactDslWithProvider builder) {
        return builder.given("test POST")
                .uponReceiving("POST request")
                .path(RESERVE_ENDPOINT)
                .method(HttpMethod.POST)
                .body(CONTRACT_CONSUMER_REQUEST)
                .headers(CONTRACT_REQUEST_HEADERS)
                .willRespondWith()
                .status(HttpStatus.SC_OK)
                .headers(CONTRACT_RESPONSE_HEADERS)
                .body(CONTRACT_PROVIDER_RESPONSE)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "testReserve", pactVersion = PactSpecVersion.V3)
    void testReserve() throws JsonProcessingException {

        // given
        ObjectMapper mapper = new ObjectMapper();
        ProductReservationRequest request =
                mapper.readValue(CONTRACT_CONSUMER_REQUEST, ProductReservationRequest.class);

        // when
        ReservationResult result =
                given().when()
                        .body(mapper.writeValueAsString(request))
                        .headers(CONTRACT_REQUEST_HEADERS)
                        .post(PROVIDER_URL)
                        .then()
                        .log()
                        .all()
                        .statusCode(HttpStatus.SC_OK)
                        .headers(CONTRACT_RESPONSE_HEADERS)
                        .extract()
                        .body()
                        .as(ReservationResult.class);

        // then
        assertThat(mapper.writeValueAsString(result))
                .isEqualToIgnoringWhitespace(CONTRACT_PROVIDER_RESPONSE);
    }
}

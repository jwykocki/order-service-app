package com.jw;

import static com.jw.OrderTestFixtures.*;
import static com.jw.resources.RequestCaller.callEndpointAndAssertStatusCode;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.jw.dto.OrderResponse;
import com.jw.dto.OrdersResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(value = CRUDOrderITConfiguration.class)
public class CRUDOrderIT {

    @Test
    public void shouldSaveOrdersToDatabaseAndReceiveThem() {

        callEndpointAndAssertStatusCode("/order", VALID_ORDER_REQUEST, 204);
        callEndpointAndAssertStatusCode("/order", VALID_ORDER_REQUEST_2, 204);

        OrdersResponse receivedResponse =
                given().when()
                        .contentType("application/json")
                        .get("/order")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(OrdersResponse.class);

        assertThat(receivedResponse.getOrders().size()).isEqualTo(2);
        List<String> orderNames =
                receivedResponse.getOrders().stream().map(OrderResponse::name).toList();
        assertThat(orderNames)
                .containsExactlyInAnyOrder(TEST_ORDER_REQUEST_NAME, TEST_ORDER_REQUEST_NAME_2);
    }
}

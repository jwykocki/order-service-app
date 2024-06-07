package com.jw.resources;

import static io.restassured.RestAssured.given;

public class RequestCaller {

    public static void callEndpointAndAssertStatusCode(String path, String body, int statusCode) {
        given().when()
                .contentType("application/json")
                .body(body)
                .post(path)
                .then()
                .statusCode(statusCode);
    }
}

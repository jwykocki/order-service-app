package com.jw.resources;

import static io.restassured.RestAssured.given;

import io.restassured.response.ResponseBody;

public class RequestCaller {

    public static ResponseBody callEndpointAndAssertStatusCodeAndReturn(
            String method, String path, String body, int statusCode) {
        return given().when()
                .contentType("application/json")
                .body(body)
                .request(method, path)
                .then()
                .log()
                .all()
                .statusCode(statusCode)
                .extract()
                .response()
                .body();
    }
}

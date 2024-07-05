package com.jw;

public class OrderTestFixtures {
    public static final String VALID_ORDER_REQUEST =
            """
            {
                "customerId": 3,
                "productOrders": [
                    {
                        "productId": 6,
                        "quantity": 2
                    },
                    {
                        "productId": 7,
                        "quantity": 4
                    }
                ]
            }
            """;

    public static final String VALID_ORDER_REQUEST_2 =
            """
            {
                "name": "testOrderName2"
            }
            """;

    public static final Long TEST_ORDER_REQUEST_CUSTOMER_ID = 123L;
    public static final Long TEST_ORDER_REQUEST_CUSTOMER_ID_2 = 456L;

    public static final String INVALID_JSON_BODY =
            """
            {
                "name": "invalidBody
            }
            """;

    public static final String BODY_WITHOUT_REQUIRED_FIELD =
            """
            {
            }
            """;

    public static final String BODY_WITH_EMPTY_FIELD =
            """
            {
                "name": ""
            }
            """;

    public static final String BODY_WITH_BLANK_FIELD =
            """
            {
                "name": "        "
            }
            """;

    public static String generateOrderUpdateRequest(Long id) {
        return """
            {
                "id": %s,
                "name": "testOrderName2"
            }
            """
                .formatted(id);
    }
}

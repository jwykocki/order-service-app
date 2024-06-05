package com.jw;

public class OrderTestFixtures {
    public static final String VALID_ORDER_REQUEST =
            """
            {
                "name": "testOrderName"
            }
            """;

    public static final String TEST_ORDER_REQUEST_NAME = "testOrderName";

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
}

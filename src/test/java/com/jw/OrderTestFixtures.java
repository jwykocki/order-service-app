package com.jw;

import com.jw.dto.OrderProductRequest;
import com.jw.dto.OrderRequest;
import java.util.List;

public class OrderTestFixtures {

    public static final Long TEST_CUSTOMER_ID_1 = 123L;
    public static final Long TEST_CUSTOMER_ID_2 = 456L;
    public static final Long TEST_PRODUCT_1_ID = 1L;
    public static final Long TEST_PRODUCT_2_ID = 2L;
    public static final int TEST_PRODUCT_1_QUANTITY = 3;
    public static final int TEST_PRODUCT_2_QUANTITY = 4;
    public static final String ORDER_ENDPOINT = "/order";
    public static final OrderProductRequest TEST_PRODUCT_1 =
            new OrderProductRequest(TEST_PRODUCT_1_ID, TEST_PRODUCT_1_QUANTITY);
    public static final OrderProductRequest TEST_PRODUCT_2 =
            new OrderProductRequest(TEST_PRODUCT_2_ID, TEST_PRODUCT_2_QUANTITY);

    public static OrderRequest testOrderRequestWithTwoProducts() {
        return new OrderRequest(TEST_CUSTOMER_ID_1, List.of(TEST_PRODUCT_1, TEST_PRODUCT_2));
    }

    public static OrderRequest testOrderRequestWithOneProduct() {
        return new OrderRequest(TEST_CUSTOMER_ID_2, List.of(TEST_PRODUCT_1));
    }
}

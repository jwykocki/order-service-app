package com.jw.exception;

public class ExceptionMessages {
    public static final String ORDER_NOT_FOUND_MESSAGE = "Order with id=%s was not found";
    public static final String ORDER_ALREADY_FINALIZED_MESSAGE =
            "Order with id=%s was already finalized";
    public static final String PRODUCT_NOT_RESERVED_MESSAGE = "Product with id=%s is not reserved";
    public static final String PRODUCT_NOT_ENOUGH_RESERVED_MESSAGE =
            "Not enough reserved for the requested quantity of product id=%s";
    public static final String REQUEST_BODY_NOT_VALID_MESSAGE = "Request body is not valid";
    public static final String ERROR_OCCURRED_MESSAGE = "An error occurred";
}

package com.jw.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessages {
    public final String ORDER_NOT_FOUND_MESSAGE = "Order with id=%s was not found";
    public final String ORDER_ALREADY_FINALIZED_MESSAGE = "Order with id=%s was already finalized";
    public final String PRODUCT_NOT_RESERVED_MESSAGE = "Product with id=%s is not reserved";
    public final String PRODUCT_NOT_ENOUGH_RESERVED_MESSAGE =
            "Not enough reserved for the requested quantity of product id=%s";
    public final String REQUEST_BODY_NOT_VALID_MESSAGE = "Request body is not valid";
    public final String ERROR_OCCURRED_MESSAGE = "An error occurred";
}

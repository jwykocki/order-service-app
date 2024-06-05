package com.jw.service;

import com.jw.entity.Order;
import com.jw.error.InvalidOrderRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrderValidator {

    public void validateOrder(Order order) {
        try {
            checkIfValid(order);
        } catch (ConstraintViolationException e) {
            List<String> violations = new ArrayList<String>();
            e.getConstraintViolations().forEach(v -> violations.add(v.getMessage()));
            throw new InvalidOrderRequestException("Request body is not valid", violations);
        }
    }

    public void checkIfValid(@Valid Order order) {}
}

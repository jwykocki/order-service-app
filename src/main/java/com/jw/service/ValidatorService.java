package com.jw.service;

import com.jw.error.InvalidOrderRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class ValidatorService {

    private final Validator validator;

    public void validate(Object object) {
        try {
            validator.validate(object);
        } catch (ConstraintViolationException e) {
            List<String> violations = new ArrayList<String>();
            e.getConstraintViolations().forEach(v -> violations.add(v.getMessage()));
            throw new InvalidOrderRequestException("Request body is not valid", violations);
        }
    }
}

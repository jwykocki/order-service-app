package com.jw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.dto.processed.ProductReservationResult;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@ApplicationScoped
public class OrderProductMapper {

    private final ObjectMapper objectMapper;

    public ProductReservationResult toProductReservationResult(String json) {
        return (ProductReservationResult) jsonToObject(json, ProductReservationResult.class);
    }

    @SneakyThrows
    private Object jsonToObject(String json, Class<?> clazz) {
        return objectMapper.readValue(json, clazz);
    }
}

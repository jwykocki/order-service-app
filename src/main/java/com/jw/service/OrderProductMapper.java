package com.jw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.dto.finalize.request.OrderFinalizeRequest;
import com.jw.dto.finalize.request.OrderProductFinalizeRequest;
import com.jw.dto.processed.ProductReservationResult;
import com.jw.dto.response.OrderResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@ApplicationScoped
public class OrderProductMapper {

    private final ObjectMapper objectMapper;

    public ProductReservationResult toProductReservationResult(String json) {
        return (ProductReservationResult) jsonToObject(json, ProductReservationResult.class);
    }

    public OrderFinalizeRequest getFinalizeRequestFromOrderResponse(OrderResponse orderResponse) {
        List<OrderProductFinalizeRequest> products =
                orderResponse.orderProducts().stream()
                        .map(p -> new OrderProductFinalizeRequest(p.productId(), p.quantity()))
                        .toList();
        return new OrderFinalizeRequest(
                orderResponse.orderId(), orderResponse.customerId(), products);
    }

    @SneakyThrows
    private Object jsonToObject(String json, Class<?> clazz) {
        return objectMapper.readValue(json, clazz);
    }
}

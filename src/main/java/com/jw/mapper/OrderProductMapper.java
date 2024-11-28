package com.jw.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.dto.finalize.request.OrderFinalizeRequest;
import com.jw.dto.finalize.request.OrderProductFinalizeRequest;
import com.jw.dto.processed.ProductReservationResult;
import com.jw.entity.Order;
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

    public OrderFinalizeRequest getFinalizeRequestFromOrder(Order order) {
        List<OrderProductFinalizeRequest> products =
                order.getOrderProducts().stream()
                        .map(
                                p ->
                                        new OrderProductFinalizeRequest(
                                                p.getProductId(), p.getQuantity()))
                        .toList();
        return new OrderFinalizeRequest(order.getOrderId(), order.getCustomerId(), products);
    }

    @SneakyThrows
    private Object jsonToObject(String json, Class<?> clazz) {
        return objectMapper.readValue(json, clazz);
    }
}

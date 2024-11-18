package com.jw.service;

import static com.jw.exception.ExceptionMessages.ORDER_NOT_FOUND_MESSAGE;

import com.jw.dto.processed.ProductReservationResult;
import com.jw.exception.OrderNotFoundException;
import com.jw.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void updateOrderProductStatus(ProductReservationResult result) {
        // TODO null handling
        productRepository
                .getByOrderIdAndProductId(result.orderId(), result.product().productId())
                .orElseThrow(
                        () ->
                                new OrderNotFoundException(
                                        ORDER_NOT_FOUND_MESSAGE.formatted(result.orderId())))
                .setStatus(result.status());
    }
}

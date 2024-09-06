package com.jw.service;

import com.jw.dto.processed.ProductReservationResult;
import com.jw.entity.OrderProduct;
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
    public void updateOrderProductStatus(ProductReservationResult productReservationResult) {
        OrderProduct product =
                productRepository.getByOrderIdAndProductIdAndQuantity(
                        productReservationResult.orderId(),
                        productReservationResult.product().productId(),
                        productReservationResult.product().quantity().longValue());
        product.setStatus(productReservationResult.status());
    }
}

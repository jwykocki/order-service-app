package com.jw.service;

import com.jw.dto.processed.ProductReservationResult;
import com.jw.entity.OrderProduct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void updateOrderProductStatus(ProductReservationResult productReservationResult) {
        // TODO null handling
        //REVIEW-VINI: Once you change to return it as Optional you can inline all these code
        OrderProduct product =
                productRepository.getByOrderIdAndProductIdAndQuantity(
                        productReservationResult.orderId(),
                        productReservationResult.product().productId(),
                        productReservationResult.product().quantity().longValue());
        product.setStatus(productReservationResult.status());
    }
}

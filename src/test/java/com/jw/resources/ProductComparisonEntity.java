package com.jw.resources;

import com.jw.constants.OrderProductStatus;
import com.jw.dto.processed.ProductReservationResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductComparisonEntity {
    private Long productId;
    private int quantity;
    private OrderProductStatus status;

    public static ProductComparisonEntity from(
            Long productId, int quantity, OrderProductStatus status) {
        ProductComparisonEntity entity = new ProductComparisonEntity();
        entity.productId = productId;
        entity.quantity = quantity;
        entity.status = status;
        return entity;
    }

    public static ProductComparisonEntity from(ProductReservationResponse response) {
        ProductComparisonEntity entity = new ProductComparisonEntity();
        entity.productId = response.productId();
        entity.quantity = response.quantity();
        entity.status = response.status();
        return entity;
    }
}

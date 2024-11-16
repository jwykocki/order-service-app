package com.jw.service;

import com.jw.entity.OrderProduct;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<OrderProduct> {

    public OrderProduct getByOrderIdAndProductIdAndQuantity(
            Long orderId, Long productId, Long quantity) {
        Map<String, Object> params = new HashMap<>();
        params.put("order", orderId);
        params.put("productId", productId);
        params.put("quantity", quantity);
        //REVIEW-VINI: Please search only by productId and Order, you should have only one of the same product on the same order, plu quantity it's a mutable value.
        return find("productId = :productId and quantity = :quantity and order.id = :order", params)
                //REVIEW-VINI: Return as Optional
                .firstResult();
    }
}

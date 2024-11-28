package com.jw.repository;

import com.jw.entity.OrderProduct;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<OrderProduct> {

    public Optional<OrderProduct> getByOrderIdAndProductId(Long orderId, Long productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("order", orderId);
        params.put("productId", productId);
        return find("productId = :productId and order.id = :order", params).firstResultOptional();
    }
}

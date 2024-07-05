package com.jw.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "order_product_table")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductOrder {
    @Id
    private Long orderId;
    @NotBlank
    private Long productId;
    @NotBlank
    private Integer quantity;
    @NotBlank
    private String status;
}

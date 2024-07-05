package com.jw.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_product_table")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderProduct {

    @ManyToOne
    @JoinColumn(name="orderid", nullable=false)
    Order order;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private int quantity;

}

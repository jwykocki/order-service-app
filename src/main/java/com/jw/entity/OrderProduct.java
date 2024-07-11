package com.jw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "order_product_table")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderProduct {

    @ManyToOne
    @JoinColumn(name = "orderid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Order order;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long productId;
    private int quantity;
}

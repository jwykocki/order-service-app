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
@ToString
public class OrderProduct {

    @ManyToOne
    @JoinColumn(name = "orderid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    //REVIEW-VINI: please add the private scope here, and move it under Id, as a good practice always keep the ID field at the
    // first to improve the readability of this entity. Thanks to it I can quickly see what I have as an ID
    Order order;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_generator")
    @SequenceGenerator(name = "product_generator", sequenceName = "product_seq", allocationSize = 1)
    private Long id;

    private Long productId;
    private int quantity;
    private String status;
}

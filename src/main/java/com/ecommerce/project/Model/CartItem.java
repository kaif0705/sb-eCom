package com.ecommerce.project.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name= "cart_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name= "cart_id")
    private Cart cart;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name= "product_id")
    private Product product;

    private Integer quantity;
    private Double discount;
    private Double productPrice;

}

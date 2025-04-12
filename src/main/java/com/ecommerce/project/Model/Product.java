package com.ecommerce.project.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min= 4 ,message= "Minimum length of productName should be 4")
    private String productName;

    @NotBlank
    @Size(min= 6 ,message= "Minimum length of productDescription should be 6")
    private String productDescription;

    private Integer productQuantity;
    private String image;
    private double price;
    private double discount;
    private double specialPrice;

    @ManyToOne
    @JoinColumn(name= "category_id")
    private Category category;

}

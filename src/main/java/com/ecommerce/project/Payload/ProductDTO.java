package com.ecommerce.project.Payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private String image;
    private String productDescription;
    private Integer productQuantity;
    private double price;
    private double discount;
    private double specialPrice;



}

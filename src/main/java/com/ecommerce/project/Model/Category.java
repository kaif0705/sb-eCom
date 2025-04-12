package com.ecommerce.project.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name= "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long categoryId;

    @NotBlank
    @Size(min= 5, message="Minimum 5 characters in needed!")
    private String categoryName;

    @OneToMany
    private List<Product> products;


//    public Category() {}

//    public Category(Long categoryId, String categoryName){
//        this.categoryId = categoryId;
//        this.categoryName = categoryName;
//    }

//    public void setCategoryId(Long categoryId){
//        this.categoryId = categoryId;
//    }
//
//    public Long getCategoryId(){
//        return categoryId;
//    }
//
//    public void setCategoryName(String categoryName){
//        this.categoryName = categoryName;
//    }
//
//    public String getCategoryName(){
//        return categoryName;
//    }

}

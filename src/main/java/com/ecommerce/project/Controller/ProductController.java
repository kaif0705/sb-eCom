package com.ecommerce.project.Controller;

import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import com.ecommerce.project.Service.ProductService;
import com.ecommerce.project.config.AppConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
            @Valid @RequestBody ProductDTO productDto,
            @PathVariable Long categoryId
    ){
        ProductDTO productDTO= productService.addProduct(productDto, categoryId);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProduct(
            @RequestParam(name= "pageNo", defaultValue= AppConstants.PAGE_NUMBER, required= false ) Integer pageNo,
            @RequestParam(name= "pageSize", defaultValue= AppConstants.PAGE_SIZE, required= false) Integer pageSize,
            @RequestParam(name="sortBy", required= false, defaultValue= AppConstants.SORT_BY_PRODUCTID) String sortBy,
            @RequestParam(name="sortOrder", required= false, defaultValue= AppConstants.SORT_ORDER) String sortOrder
            ){
        ProductResponse response= productService.getAllProduct(pageNo, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name= "pageNo", defaultValue= AppConstants.PAGE_NUMBER, required= false ) Integer pageNo,
            @RequestParam(name= "pageSize", defaultValue= AppConstants.PAGE_SIZE, required= false) Integer pageSize,
            @RequestParam(name="sortBy", required= false, defaultValue= AppConstants.SORT_BY_PRODUCTID) String sortBy,
            @RequestParam(name="sortOrder", required= false, defaultValue= AppConstants.SORT_ORDER) String sortOrder
    ){
        ProductResponse response= productService.getProductsByCategory(categoryId, pageNo, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(
            @PathVariable String keyword,
            @RequestParam(name= "pageNo", defaultValue= AppConstants.PAGE_NUMBER, required= false ) Integer pageNo,
            @RequestParam(name= "pageSize", defaultValue= AppConstants.PAGE_SIZE, required= false) Integer pageSize,
            @RequestParam(name="sortBy", required= false, defaultValue= AppConstants.SORT_BY_PRODUCTID) String sortBy,
            @RequestParam(name="sortOrder", required= false, defaultValue= AppConstants.SORT_ORDER) String sortOrder
    ){
        ProductResponse response= productService.getProductsByKeyword(keyword, pageNo, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDto){
        ProductDTO response= productService.updateProduct(productId, productDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId){
        ProductDTO response= productService.deleteProduct(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image){
        ProductDTO productDTO= productService.updateImage(productId, image);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

}

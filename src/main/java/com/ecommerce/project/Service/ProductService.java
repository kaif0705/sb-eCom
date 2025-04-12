package com.ecommerce.project.Service;

import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    ProductDTO addProduct(ProductDTO ProductDto, Long categoryId);
    ProductResponse getAllProduct(Integer pageNo, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse getProductsByCategory(Long categoryId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse getProductsByKeyword(String keyword, Integer pageNo, Integer pageSize, String sortBy, String sortOrder);
    ProductDTO updateProduct(Long categoryId, ProductDTO productDTO);
    ProductDTO deleteProduct(Long productId);
    ProductDTO updateImage(Long productId, MultipartFile image);

}

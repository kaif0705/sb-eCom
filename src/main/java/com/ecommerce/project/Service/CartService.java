package com.ecommerce.project.Service;

import com.ecommerce.project.Payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);
    List<CartDTO> getAllCarts();
    CartDTO getUserCart(String email, Long cartId);

    @Transactional
    CartDTO updateProductQuantity(Long productId, Integer quantity);

    @Transactional
    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}

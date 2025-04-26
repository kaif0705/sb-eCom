package com.ecommerce.project.Controller;

import com.ecommerce.project.Model.Cart;
import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.Service.CartService;
import com.ecommerce.project.Util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/cart/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDTO productAdded= cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(productAdded, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> response= cartService.getAllCarts();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("carts/users/cart")
    public ResponseEntity<CartDTO> getUserCart() {
        String email= authUtil.loggedInEmail();
        Cart cart= cartRepository.findCartByEmail(email);
        CartDTO response= cartService.getUserCart(email, cart.getCartId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> updateProductQuantity(@PathVariable Long productId, @PathVariable String quantity) {
        CartDTO response= cartService.updateProductQuantity(productId,
                quantity.equalsIgnoreCase("delete") ? -1 : 1
                );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String response= cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

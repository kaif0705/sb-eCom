package com.ecommerce.project.Repository;

import com.ecommerce.project.Model.Cart;
import com.ecommerce.project.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c FROM Cart c WHERE c.user.email= ?1 AND c.cartId= ?2")
    Cart findCartByEmailAndCartId(String email, Long cartId);

    @Query("SELECT c FROM Cart c WHERE c.cartId= ?1 AND c.user.email= ?2")
    List<Product> findProductsByCartIdAndEmail(Long cartId, String email);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItem ci JOIN FETCH ci.product p WHERE p.productId= ?1")
    List<Cart> findCartsByProductId(Long productId);
}

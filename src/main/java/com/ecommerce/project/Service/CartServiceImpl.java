package com.ecommerce.project.Service;

import com.ecommerce.project.Model.Cart;
import com.ecommerce.project.Model.CartItem;
import com.ecommerce.project.Model.Product;
import com.ecommerce.project.Model.User;
import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Repository.CartItemRepository;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.Repository.ProductRepository;
import com.ecommerce.project.Util.AuthUtil;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthUtil authUtils;

    @Autowired
    private ModelMapper modelMapper;

    public CartDTO addProductToCart(Long productId, Integer quantity){

        //Find existing cart or create a new one
        Cart cart= createCart();

        //Retrieve product details from productId(price, quantity, stock, etc)
        Product product= productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));


        //Perform Validations(if product is already in the cart, if product is available then only add it to cart)
        //Check quantity in DB
        if(product.getProductQuantity() == null){
                throw new APIException(product.getProductName() + " is not available");
        }

        //Check if product already exists in cart
        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if(cartItem != null){
            throw new APIException(product.getProductName() + " is already in the cart");
        }

        //If user is ordering more than the quantity present
        if(quantity > product.getProductQuantity()){
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getProductQuantity() + ".");
        }


        //Create cartItem, after all validations are performed we need to add product to cart and create CartItem obj
        //Create new CartItem
        CartItem newCartItem= new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setCart(cart);

        //Save CartItem
        cart.getCartItem().add(newCartItem);
        cartItemRepository.save(newCartItem);

        //Reduce the product quantity from the DB once order is placed
        product.setProductQuantity(product.getProductQuantity());

        //Update the cart price
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        //return updated cart
        CartDTO cartDTO= modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems= cart.getCartItem();
        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setProductQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());


        return cartDTO;
    }

    public List<CartDTO> getAllCarts(){

        List<Cart> carts= cartRepository.findAll();

        List<CartDTO> cartDTO= carts.stream().map(
                cart -> {
                    CartDTO cartDTOs= modelMapper.map(cart, CartDTO.class);

                    List<ProductDTO> productDTO= cart.getCartItem().stream().map(
                            cartItem -> {
                                ProductDTO products= modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                                products.setProductQuantity(cartItem.getQuantity());
                                return products;
                            }
                     ).toList();

                    cartDTOs.setProducts(productDTO);
                    return cartDTOs;
                }
        ).toList();

        return cartDTO;
    }

    public CartDTO getUserCart(String email, Long cartId){

        Cart cart= cartRepository.findCartByEmailAndCartId(email, cartId);

        if(cart == null){
            throw new ResourceNotFoundException("Cart", "email", email);
        }

        //Setting product quantity for each product
        cart.getCartItem().forEach(
                cartItem -> cartItem.getProduct().setProductQuantity(cartItem.getQuantity())
        );

        //Extracting products from cart and mapping it to ProductDTO
        List<ProductDTO> products= cart.getCartItem().stream().map(
                cartItem -> modelMapper.map(cartItem.getProduct(), ProductDTO.class)
        ).toList();

        CartDTO cartDTO= modelMapper.map(cart, CartDTO.class);
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    public CartDTO updateProductQuantity(Long productId, Integer quantity){
        User user= authUtils.loggedInUser();
        Cart userCart= cartRepository.findCartByEmail(user.getEmail());

        Product product= productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //perform validations
        if(product.getProductQuantity() == 0){
            throw new APIException(product.getProductName() + " not available");
        }

        if(quantity > product.getProductQuantity()){
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getProductQuantity() + ".");
        }

        //Get cart item
        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(productId, userCart.getCartId());
        if(cartItem == null){
            throw new ResourceNotFoundException("CartItem", "cartId", userCart.getCartId());
        }

        //cal new quantity
        int updatedQuantity= cartItem.getQuantity() + quantity;

        //Validations to perform -ve quantity
        if(updatedQuantity < 0){
            throw new APIException("Please, make an order of the " + product.getProductName());
        }

        if(updatedQuantity == 0){
            deleteProductFromCart(userCart.getCartId(), productId);
        }else {

            //Update cartItem
            cartItem.setQuantity(updatedQuantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getSpecialPrice());

            //Update cart
            userCart.setTotalPrice(userCart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(userCart);
        }


        //save cart item
        CartItem updatedCartItem= cartItemRepository.save(cartItem);
        if(updatedCartItem.getQuantity() == 0){
            deleteProductFromCart(userCart.getCartId(), productId);
        }


        //Sending CartDTO Response
        CartDTO cartDTO= modelMapper.map(userCart, CartDTO.class);

        List<ProductDTO> products= userCart.getCartItem().stream().map(
                c -> {
                    ProductDTO productDTO= modelMapper.map(c.getProduct(), ProductDTO.class);
                    productDTO.setProductQuantity(c.getQuantity());
                    return productDTO;
                }
        ).toList();

        cartDTO.setProducts(products);

        return cartDTO;
    }

    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId){

        User user= authUtils.loggedInUser();
        Cart cart= cartRepository.findCartByEmail(user.getEmail());

        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if(cartItem == null){
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        //Update cart price
        cart.setTotalPrice(
                cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity())
        );

        cartRepository.save(cart);

        //Deleting cart item from repository
        cartItemRepository.deleteCartItemByProductIdAndCartId(productId, cartId);
        cartItemRepository.flush();

        return "Product " + cartItem.getProduct().getProductName() + " Removed Successfully!!";
    }

    public void updateProductInCarts(Long cartId, Long productId){
        Cart cart= cartRepository.findById(cartId).
                orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product= productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);

        if(cartItem == null){
            throw new ResourceNotFoundException("CartItem", "cartId", cartId);
        }

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartRepository.save(cart);
    }

    private Cart createCart(){
        //Retrieve cart if already exists
        Cart userCart= cartRepository.findCartByEmail(authUtils.loggedInEmail());
        if(userCart != null){
            return userCart;
        }

        //Create new Cart
        Cart cart= new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtils.loggedInUser());
        Cart newCart= cartRepository.save(cart);
        return newCart;
    }

}

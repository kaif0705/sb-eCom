package com.ecommerce.project.Service;

import com.ecommerce.project.Model.*;
import com.ecommerce.project.Payload.OrderDTO;
import com.ecommerce.project.Payload.OrderItemDTO;
import com.ecommerce.project.Repository.*;
import com.ecommerce.project.Util.AuthUtil;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AuthUtil authUtils;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage){

        //Fetch user cart
        Cart cart= cartRepository.findCartByEmail(email);

        //Fetch User address
        Address address= addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "id", addressId)
        );

        //Create Order obj
        Order order= new Order();
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        //Create Payment information
        Payment payment= new Payment(paymentMethod, pgName, pgPaymentId, pgStatus, pgResponseMessage);
        payment.setOrder(order);
        order.setPayment(payment);

        //Save Payment and order
        Order savedorder= orderRepository.save(order);
        paymentRepository.save(payment);

        //Map orderItem with CartItems
        List<CartItem> cartItems= cart.getCartItem();

        if(cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems= new ArrayList<>();

        for(CartItem cartItem : cartItems){
            OrderItem orderItem= new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setOrder(savedorder);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        //Reduce stock from DB and delete cart
        List<CartItem> snapshot = new ArrayList<>( cart.getCartItem() );

        for (CartItem ci : snapshot) {
            int qty = ci.getQuantity();
            Product p = ci.getProduct();
            p.setProductQuantity( p.getProductQuantity() - qty );
            productRepository.save(p);

            // now delete safely – you’re not iterating the original list any more
            cartService.deleteProductFromCart(ci.getCartItemId(), p.getProductId());
        }

        //Send response
        OrderDTO orderDTO= new OrderDTO();
        orderItems.forEach(
                item -> {
                        orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class));
                }
        );

        orderDTO.setAddressId(addressId);

        return orderDTO;

    }

}

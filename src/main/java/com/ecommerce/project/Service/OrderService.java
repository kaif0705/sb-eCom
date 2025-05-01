package com.ecommerce.project.Service;

import com.ecommerce.project.Payload.OrderDTO;

public interface OrderService {

    OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);

}

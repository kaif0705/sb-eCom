package com.ecommerce.project.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name= "payments")
public class Payment {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy= "payment" ,cascade= {CascadeType.MERGE, CascadeType.PERSIST})
    private Order order;

    @NotBlank
    @Size(min= 4, message= "Payment method should contain atleast 4 Characters")
    private String paymentMethod;

    private String pgPaymentId;
    private String pgStatus;
    private String pgMessageResponse;

    private String pgName;


    public Payment(String paymentMethod, String pgPaymentId, String pgStatus, String pgResponseMessage, String pgName) {
        this.paymentMethod = paymentMethod;
        this.pgPaymentId = pgPaymentId;
        this.pgStatus = pgStatus;
        this.pgMessageResponse = pgResponseMessage;
        this.pgName = pgName;
    }
}

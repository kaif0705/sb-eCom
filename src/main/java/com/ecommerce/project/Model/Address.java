package com.ecommerce.project.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    public Address(String street, String city, String buildingName, String state, String country, String pincode) {
        this.street = street;
        this.city = city;
        this.buildingName = buildingName;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "address_id")
    private Long addressId;

    @NotBlank
    @Size(min=5, message= "Street name must be atleast 5 characters")
    private String street;

    @NotBlank
    @Size(min=5, message= "City name must be atleast 5 characters")
    private String city;

    @NotBlank
    @Size(min=4, message= "Building Name name must be atleast 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min=2, message= "State Name name must be atleast 2 characters")
    private String state;

    @NotBlank
    @Size(min=2, message= "Country Name name must be atleast 2 characters")
    private String country;

    @NotBlank
    @Size(min=6, message= "Pincode Name name must be atleast 6 characters")
    private String pincode;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name= "user_id")
    private User users;
}

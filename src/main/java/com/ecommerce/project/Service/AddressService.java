package com.ecommerce.project.Service;

import com.ecommerce.project.Model.Address;
import com.ecommerce.project.Payload.AddressDTO;

import java.util.List;

public interface AddressService {

    AddressDTO createAddress(AddressDTO address);
    List<AddressDTO> getAllAddress();
    AddressDTO getAddress(Long addressId);
    List<AddressDTO> getUserAddresses();
    String deleteAddress(Long addressId);
    AddressDTO updateAddress(Long addressId, Address address);
}

package com.ecommerce.project.Controller;

import com.ecommerce.project.Model.Address;
import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO address){
        AddressDTO response= addressService.createAddress(address);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddress(){
        List<AddressDTO> response= addressService.getAllAddress();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId){
        AddressDTO response= addressService.getAddress(addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> userAddresses(){
        List<AddressDTO> response= addressService.getUserAddresses();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String response= addressService.deleteAddress(addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId, @RequestBody Address address){
        AddressDTO response= addressService.updateAddress(addressId, address);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

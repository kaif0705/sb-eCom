package com.ecommerce.project.Service;

import com.ecommerce.project.Model.Address;
import com.ecommerce.project.Model.User;
import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.Repository.AddressRepository;
import com.ecommerce.project.Repository.UserRepository;
import com.ecommerce.project.Util.AuthUtil;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtil authUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO){

        Address address= modelMapper.map(addressDTO, Address.class);
        User user= authUtils.loggedInUser();

        //Get all existing addresses from user
        List<Address> addresses= user.getAddresses();
        //Add new address to list
        addresses.add(address);
        //Set new address to the user
        address.setUsers(user);

        //Bi-directional mapping
        user.setAddresses(addresses);

        //Save
        addressRepository.save(address);
        userRepository.save(user);

        //Response
        AddressDTO response= modelMapper.map(address, AddressDTO.class);
        return response;
    }

    @Override
    public List<AddressDTO> getAllAddress(){
        List<Address> addresses= addressRepository.findAll();

        List<AddressDTO> addressDTO= addresses.stream().map(
                address -> modelMapper.map(address ,AddressDTO.class)
        ).toList();

        return addressDTO;

    }

    @Override
    public AddressDTO getAddress(Long addressId){
        Address address= addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(){
        User user= authUtils.loggedInUser();

        List<Address> addresses= user.getAddresses();

        List<AddressDTO> addressDTO= addresses.stream().map(
                address -> modelMapper.map(address, AddressDTO.class)
        ).toList();

        return addressDTO;
    }

    @Override
    public String deleteAddress(Long addressId){
        Address address= addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        addressRepository.delete(address);

        //User side delete
        User user= authUtils.loggedInUser();
        //Remove the existing Address
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        userRepository.save(user);

        return "Address successfully deleted";
    }

    @Override
    public AddressDTO updateAddress(Long addressId, Address address){

        Address addressFromDB= addressRepository.findById(addressId).
        orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        addressFromDB.setStreet(address.getStreet());
        addressFromDB.setCity(address.getCity());
        addressFromDB.setBuildingName(address.getBuildingName());
        addressFromDB.setState(address.getState());
        addressFromDB.setCountry(address.getCountry());
        addressFromDB.setPincode(address.getPincode());

        Address savedAddress= addressRepository.save(addressFromDB);

        //Update user side address
        User user = authUtils.loggedInUser();
        user.getAddresses().removeIf((addressToRemove) -> addressToRemove.getAddressId().equals(addressId));
        user.getAddresses().add(addressFromDB);
        userRepository.save(user);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

}

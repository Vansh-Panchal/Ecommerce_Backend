package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.model.Address;
import com.example.demo.model.User;
import com.example.demo.repository.AddressRepository;


@Service
public class AddressServiceImplementation implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressServiceImplementation(AddressRepository addressRepository,
                                        UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    @Override
    public Address addAddress(String jwt, Address address) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        address.setUser(user);

        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(String jwt, Long addressId, Address address) throws Exception {

        Address existing = addressRepository.findById(addressId).orElseThrow();

        existing.setFirstName(address.getFirstName());
        existing.setLastName(address.getLastName());
        existing.setStreetAdress(address.getStreetAdress());
        existing.setCity(address.getCity());
        existing.setState(address.getState());
        existing.setZipCode(address.getZipCode());
        existing.setMobile(address.getMobile());

        return addressRepository.save(existing);
    }

    @Override
    public void deleteAddress(String jwt, Long addressId) throws Exception {

        try{
            addressRepository.deleteById(addressId);
        }
        catch(Exception e){
            throw new Exception("Cannot delete address because it is used in an order");
        }
    }
}
package com.example.demo.service;

import com.example.demo.model.Address;

public interface AddressService {

    Address addAddress(String jwt, Address address) throws Exception;

    Address updateAddress(String jwt, Long addressId, Address address) throws Exception;

    void deleteAddress(String jwt, Long addressId) throws Exception;

}
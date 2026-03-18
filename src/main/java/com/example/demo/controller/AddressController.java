package com.example.demo.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Address;
import com.example.demo.service.AddressService;

@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = "http://localhost:5173")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // ADD ADDRESS
    @PostMapping
    public ResponseEntity<Address> addAddress(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Address address
    ) throws Exception {

        Address savedAddress = addressService.addAddress(jwt.substring(7), address);
        return ResponseEntity.ok(savedAddress);
    }

    // UPDATE ADDRESS
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id,
            @RequestBody Address address
    ) throws Exception {

        Address updatedAddress = addressService.updateAddress(jwt.substring(7), id, address);
        return ResponseEntity.ok(updatedAddress);
    }

    // DELETE ADDRESS
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception {

        addressService.deleteAddress(jwt.substring(7), id);
        return ResponseEntity.ok("Address deleted");
    }
}
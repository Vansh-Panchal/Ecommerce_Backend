package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	@Query("SELECT a FROM Address a WHERE a.user.id = :userId")
	public List<Address> findByUserId(@Param("userId") Long userId);
	
	@Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.id = :addressId")
	public Address findByUserIdAndAddressId(@Param("userId") Long userId, @Param("addressId") Long addressId);
}

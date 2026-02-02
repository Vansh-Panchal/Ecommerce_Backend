package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
	public List<Order> findByUserId(@Param("userId") Long userId);
	
	Optional<Order> findById(Long orderId);

	Optional<Order> findByPaymentDetails_PaymentLinkId(String paymentLinkId);

}

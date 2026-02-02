package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	@Query("SELECT oi FROM OrderItem oi WHERE oi.order = :order")
	public List<OrderItem> findByOrder(@Param("order") Order order);
	
	@Query("SELECT oi FROM OrderItem oi WHERE oi.userId = :userId")
	public List<OrderItem> findByUserId(@Param("userId") Long userId);
	
	@Query("SELECT oi FROM OrderItem oi WHERE oi.order = :order AND oi.product = :product "
			+ "AND oi.size = :size AND oi.userId = :userId")
	public OrderItem isOrderItemExist(@Param("order") Order order,
			@Param("product") Product product, @Param("size") String size, 
			@Param("userId") Long userId);
	
	@Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
	public List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
}

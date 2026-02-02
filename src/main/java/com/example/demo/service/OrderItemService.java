package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.OrderException;
import com.example.demo.exception.UserException;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;

public interface OrderItemService {

	public OrderItem createOrderItem(OrderItem orderItem);
	
	public OrderItem updateOrderItem(Long userId, Long id, OrderItem orderItem) throws OrderException, UserException;
	
	public OrderItem findOrderItemById(Long orderItemId) throws OrderException;
	
	public List<OrderItem> findOrderItemsByOrder(Order order);
	
	public List<OrderItem> findOrderItemsByOrderId(Long orderId) throws OrderException;
	
	public List<OrderItem> findOrderItemsByUserId(Long userId);
	
	public void removeOrderItem(Long userId, Long orderItemId) throws OrderException, UserException;
	
	public OrderItem isOrderItemExist(Order order, Product product, String size, Long userId);
}

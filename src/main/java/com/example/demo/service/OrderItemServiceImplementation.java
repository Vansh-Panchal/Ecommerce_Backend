 package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.exception.OrderException;
import com.example.demo.exception.UserException;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;

@Service
public class OrderItemServiceImplementation implements OrderItemService{
	
	private OrderItemRepository orderItemRepository;
	private UserService userService;
	private OrderRepository orderRepository;
	
	public OrderItemServiceImplementation(OrderItemRepository orderItemRepository,
			UserService userService, OrderRepository orderRepository) {
		this.orderItemRepository = orderItemRepository;
		this.userService = userService;
		this.orderRepository = orderRepository;
	}

	@Override
	public OrderItem createOrderItem(OrderItem orderItem) {
		
		// Calculate price and discounted price if not set
		if (orderItem.getPrice() == null) {
			orderItem.setPrice(orderItem.getProduct().getPrice() * orderItem.getQuantity());
		}
		if (orderItem.getDiscountedPrice() == null) {
			orderItem.setDiscountedPrice(orderItem.getProduct().getDiscountedPrice() * orderItem.getQuantity());
		}
		
		OrderItem createdOrderItem = orderItemRepository.save(orderItem);
		return createdOrderItem;
	}

	@Override
	public OrderItem updateOrderItem(Long userId, Long id, OrderItem orderItem) throws OrderException, UserException {
		
		OrderItem item = findOrderItemById(id);
		User user = userService.findUserById(userId);
		
		if(user.getId().equals(userId))
		{
			if (orderItem.getQuantity() > 0) {
				item.setQuantity(orderItem.getQuantity());
			}
			if (orderItem.getSize() != null) {
				item.setSize(orderItem.getSize());
			}
			// Recalculate prices
			item.setPrice(item.getQuantity() * item.getProduct().getPrice());
			item.setDiscountedPrice(item.getProduct().getDiscountedPrice() * item.getQuantity());
		}
		
		return orderItemRepository.save(item);
	}

	@Override
	public OrderItem findOrderItemById(Long orderItemId) throws OrderException {
		
		Optional<OrderItem> opt = orderItemRepository.findById(orderItemId);
		
		if(opt.isPresent())
		{
			return opt.get();
		}
		throw new OrderException("Order Item not found with id: " + orderItemId);
	}

	@Override
	public List<OrderItem> findOrderItemsByOrder(Order order) {
		return orderItemRepository.findByOrder(order);
	}

	@Override
	public List<OrderItem> findOrderItemsByOrderId(Long orderId) throws OrderException {
		Optional<Order> orderOpt = orderRepository.findById(orderId);
		if (!orderOpt.isPresent()) {
			throw new OrderException("Order not found with id: " + orderId);
		}
		return orderItemRepository.findByOrder(orderOpt.get());
	}

	@Override
	public List<OrderItem> findOrderItemsByUserId(Long userId) {
		return orderItemRepository.findByUserId(userId);
	}

	@Override
	public void removeOrderItem(Long userId, Long orderItemId) throws OrderException, UserException {
		
		OrderItem orderItem = findOrderItemById(orderItemId);
		
		User user = userService.findUserById(orderItem.getUserId());
		
		User reqUser = userService.findUserById(userId);
		
		if(user.getId().equals(reqUser.getId()))
		{
			orderItemRepository.deleteById(orderItemId);
		}
		else {
			throw new UserException("You can't remove other user's order item");
		}
	}

	@Override
	public OrderItem isOrderItemExist(Order order, Product product, String size, Long userId) {
		
		OrderItem orderItem = orderItemRepository.isOrderItemExist(order, product, size, userId);
		return orderItem;
	}

}

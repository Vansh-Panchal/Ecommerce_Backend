package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.exception.OrderException;
import com.example.demo.model.Address;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.User;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;

@Service
public class OrderServiceImplementation implements OrderService{

	private OrderRepository orderRepository;
	private CartRepository cartRepository;
	private CartService cartService;
	private ProductService productService;
	private AddressRepository addressRepository;
	private OrderItemRepository orderItemRepository;
	
	public OrderServiceImplementation(OrderRepository orderRepository,
			CartRepository cartRepository, CartService cartService,
			ProductService productService, AddressRepository addressRepository,
			OrderItemRepository orderItemRepository) {
		
		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.cartService = cartService;
		this.productService = productService;
		this.addressRepository = addressRepository;
		this.orderItemRepository = orderItemRepository;
	}
	
	@Override
	public Order createOrder(User user, Address shippingAddress) {
		
		// Save shipping address if it doesn't have an ID
		if (shippingAddress.getId() == null) {
			shippingAddress.setUser(user);
			shippingAddress = addressRepository.save(shippingAddress);
		}
		
		// Get user's cart
		Cart cart = cartService.findUserCart(user.getId());
		
		if (cart == null || cart.getCartItems().isEmpty()) {
			throw new RuntimeException("Cart is empty. Cannot create order.");
		}
		
		// Create order
		Order order = new Order();
		order.setUser(user);
		order.setOrderId(UUID.randomUUID().toString());
		order.setShippingAddress(shippingAddress);
		order.setOrderDate(LocalDateTime.now());
		order.setDeliveryDate(LocalDateTime.now().plusDays(7));
		order.setOrderStatus("PENDING");
		order.setCreatedAt(LocalDateTime.now());
		
		// Convert cart items to order items
		List<OrderItem> orderItems = new ArrayList<>();
		double totalPrice = 0;
		double totalDiscountedPrice = 0;
		int totalItem = 0;
		
		for (CartItem cartItem : cart.getCartItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setSize(cartItem.getSize());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getPrice());
			orderItem.setDiscountedPrice(cartItem.getDiscountedPrice());
			orderItem.setUserId(cartItem.getUserId());
			orderItem.setDeliveryDate(LocalDateTime.now().plusDays(7));
			
			orderItems.add(orderItem);
			totalPrice += cartItem.getPrice();
			totalDiscountedPrice += cartItem.getDiscountedPrice();
			totalItem += cartItem.getQuantity();
		}
		
		order.setOrderItems(orderItems);
		order.setTotalPrice(totalPrice);
		order.setTotalDiscountedPrice(totalDiscountedPrice);
		order.setTotalItem(totalItem);
		order.setDiscount((int)(totalPrice - totalDiscountedPrice));
		
		// Save order (cascade will save order items)
		Order savedOrder = orderRepository.save(order);
		
		// Clear the cart after order is created
		cart.getCartItems().clear();
		cartRepository.save(cart);
		
		return savedOrder;
	}

	@Override
	public Order findOrderById(Long orderId) throws OrderException {
		Optional<Order> opt = orderRepository.findById(orderId);
		
		if (opt.isPresent()) {
			return opt.get();
		}
		throw new OrderException("Order not found with id: " + orderId);
	}

	@Override
	public List<Order> usersOrderHistory(Long userId) {
		return orderRepository.findByUserId(userId);
	}

	@Override
	public Order placedOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus("PLACED");
		return orderRepository.save(order);
	}

	@Override
	public Order confirmedOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus("CONFIRMED");
		return orderRepository.save(order);
	}

	@Override
	public Order shippedOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus("SHIPPED");
		return orderRepository.save(order);
	}

	@Override
	public Order deliveredOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus("DELIVERED");
		order.setDeliveryDate(LocalDateTime.now());
		return orderRepository.save(order);
	}

	@Override
	public Order canceledOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus("CANCELED");
		return orderRepository.save(order);
	}

	@Override
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Order deleteOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		orderRepository.delete(order);
		return order;
	}

	
}

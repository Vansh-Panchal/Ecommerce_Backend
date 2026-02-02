package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.OrderException;
import com.example.demo.exception.UserException;
import com.example.demo.model.Address;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    private User getUserFromAuthHeader(String authHeader) throws UserException {
        String jwt = authHeader.substring(7);
        return userService.findUserProfileByJwt(jwt);
    }

    // ✅ Create order for current user
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Address shippingAddress
    ) throws UserException {
        User user = getUserFromAuthHeader(authHeader);
        Order order = orderService.createOrder(user, shippingAddress);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    
    // ✅ Get order by id
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) throws OrderException {
        Order order = orderService.findOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // ✅ Get current user's order history
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(@RequestHeader("Authorization") String authHeader) throws UserException {
        User user = getUserFromAuthHeader(authHeader);
        List<Order> orders = orderService.usersOrderHistory(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // ✅ Admin: get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // ✅ Admin: update order status helpers
    @PutMapping("/{orderId}/placed")
    public ResponseEntity<Order> markPlaced(@PathVariable Long orderId) throws OrderException {
        return new ResponseEntity<>(orderService.placedOrder(orderId), HttpStatus.OK);
    }

    @PutMapping("/{orderId}/confirmed")
    public ResponseEntity<Order> markConfirmed(@PathVariable Long orderId) throws OrderException {
        return new ResponseEntity<>(orderService.confirmedOrder(orderId), HttpStatus.OK);
    }

    @PutMapping("/{orderId}/shipped")
    public ResponseEntity<Order> markShipped(@PathVariable Long orderId) throws OrderException {
        return new ResponseEntity<>(orderService.shippedOrder(orderId), HttpStatus.OK);
    }

    @PutMapping("/{orderId}/delivered")
    public ResponseEntity<Order> markDelivered(@PathVariable Long orderId) throws OrderException {
        return new ResponseEntity<>(orderService.deliveredOrder(orderId), HttpStatus.OK);
    }

    @PutMapping("/{orderId}/canceled")
    public ResponseEntity<Order> markCanceled(@PathVariable Long orderId) throws OrderException {
        return new ResponseEntity<>(orderService.canceledOrder(orderId), HttpStatus.OK);
    }

    // ✅ Admin: delete order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Long orderId) throws OrderException {
        Order deleted = orderService.deleteOrder(orderId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}


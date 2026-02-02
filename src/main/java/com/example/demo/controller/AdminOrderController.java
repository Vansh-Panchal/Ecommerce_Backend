package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.OrderException;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Admin: get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    // Admin: update order statuses
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

    // Admin: delete order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Long orderId) throws OrderException {
        Order deleted = orderService.deleteOrder(orderId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}


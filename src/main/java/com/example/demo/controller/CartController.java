package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.ProductException;
import com.example.demo.exception.UserException;
import com.example.demo.model.Cart;
import com.example.demo.model.User;
import com.example.demo.request.AddItemRequest;
import com.example.demo.service.CartService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;
	private final UserService userService;
	
	public CartController(CartService cartService,UserService userService) {
		this.cartService = cartService;
		this.userService = userService;
	}
	
	// Get current user cart
	@GetMapping
	public ResponseEntity<Cart> getUserCart(@RequestHeader("Authorization") String authHeader) throws UserException{
		// Remove "Bearer " from jwt token
		String jwt = authHeader.substring(7);
		User user = userService.findUserProfileByJwt(jwt);
		Cart cart = cartService.findUserCart(user.getId());
		return new ResponseEntity<>(cart, HttpStatus.OK);
	}
	
	// Add to cart
	@PostMapping("/add")
	public ResponseEntity<String> addItemToCart(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody AddItemRequest request) throws UserException, ProductException{
		
		String jwt = authHeader.substring(7);
		User user = userService.findUserProfileByJwt(jwt);
		String message = cartService.addCartItem(user.getId(), request);
		return new ResponseEntity<>(message,HttpStatus.OK);
	}
	
}


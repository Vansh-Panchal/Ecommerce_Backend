package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.CartItemException;
import com.example.demo.exception.UserException;
import com.example.demo.model.CartItem;
import com.example.demo.model.User;
import com.example.demo.service.CartItemService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/cart-items")
@CrossOrigin
public class CartItemController {

    private final CartItemService cartItemService;
    private final UserService userService;

    public CartItemController(CartItemService cartItemService, UserService userService) {
        this.cartItemService = cartItemService;
        this.userService = userService;
    }

    private User getUserFromAuthHeader(String authHeader) throws UserException {
        String jwt = authHeader.substring(7);
        return userService.findUserProfileByJwt(jwt);
    }
    

    // ✅ Update cart item (e.g., quantity, size)
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long cartItemId,
            @RequestBody CartItem cartItem
    ) throws CartItemException, UserException {
        User user = getUserFromAuthHeader(authHeader);
        CartItem updated = cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // ✅ Remove cart item
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long cartItemId
    ) throws CartItemException, UserException {
        User user = getUserFromAuthHeader(authHeader);
        cartItemService.removeCartItem(user.getId(), cartItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


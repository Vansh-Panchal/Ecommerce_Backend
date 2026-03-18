package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.UserException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ✅ Get logged-in user profile using JWT
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(
            @RequestHeader("Authorization") String authHeader
    ) throws UserException {

        // Remove "Bearer " prefix
        String jwt = authHeader.substring(7);

        User user = userService.findUserProfileByJwt(jwt);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // ✅ Get user by ID (Admin / Internal use)
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(
            @PathVariable Long userId
    ) throws UserException {

        User user = userService.findUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    
    // Update Personal details
    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(
    		@RequestHeader("Authorization") String authHeader,
    		@RequestBody User updateUser)throws UserException{
    	String jwt = authHeader.substring(7);
    	User user = userService.findUserProfileByJwt(jwt);
    	user.setFirstName(updateUser.getFirstName());
    	user.setLastName(updateUser.getLastName());
    	user.setMobile(updateUser.getMobile());
    	User savedUser = userService.saveUser(user);
    	return new ResponseEntity<>(savedUser,HttpStatus.OK);
    }
}

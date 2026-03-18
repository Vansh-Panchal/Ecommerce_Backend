package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin
public class AdminUserController {

	private UserService userService;

	public AdminUserController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	 @GetMapping
	    public ResponseEntity<List<User>> getAllUsers(){
	        return ResponseEntity.ok(userService.getAllUsers());
	    }
	 
	 @DeleteMapping("/{id}")
	    public ResponseEntity<String> deleteUser(@PathVariable Long id){

	        userService.deleteUser(id);

	        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
	    }
	 @GetMapping("/{id}")
	 public ResponseEntity<User> getUserById(@PathVariable Long id) throws Exception {

	     User user = userService.findUserById(id);

	     return new ResponseEntity<>(user, HttpStatus.OK);
	 }
}

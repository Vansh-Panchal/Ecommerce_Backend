package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.UserException;
import com.example.demo.model.User;

public interface UserService {

	public User findUserById(Long userId) throws UserException;
	
	public User saveUser(User user) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public List<User> getAllUsers();
	
	public void deleteUser(Long userId);
	
	public User getUserById(Long userId);
}

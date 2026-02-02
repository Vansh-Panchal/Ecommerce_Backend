package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.config.JwtProvider;
import com.example.demo.exception.UserException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserServieImplementation implements UserService{

	private UserRepository userRepository;
	private JwtProvider jwtProvider;
	
	
	public UserServieImplementation(UserRepository userRepository, JwtProvider jwtProvider) {
//		super();
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
	}

	@Override
	public User findUserById(Long userId) throws UserException {
		Optional<User> user = userRepository.findById(userId);
		if(user.isPresent())
		{
			return user.get();
		}
		throw new UserException("User Not found with id " + userId);
		
	}

	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
		
		// Accept either raw JWT or "Bearer <jwt>"
		String token = jwt;
		if (token == null || token.isBlank()) {
			throw new UserException("Missing JWT token");
		}
		if (!token.startsWith("Bearer ")) {
			token = "Bearer " + token;
		}
		
		String email = jwtProvider.getEmailFromToken(token);
		User user = userRepository.findByEmail(email);
		if(user == null)
				throw new UserException("User not found with email "+email);
		
		return user;
	}

}

package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtProvider;
import com.example.demo.exception.UserException;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.response.AuthResponse;
import com.example.demo.service.CartService;
import com.example.demo.service.CustomeUserServiceImplementation;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomeUserServiceImplementation customUserService;
    private final JwtProvider jwtProvider;
    private final CartService cartService;

    public AuthController(UserRepository userRepository,
                          CustomeUserServiceImplementation customUserService,
                          PasswordEncoder passwordEncoder,
                          JwtProvider jwtProvider,
                          CartService cartService) {
        this.userRepository = userRepository;
        this.customUserService = customUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.cartService = cartService;
    }

    // SIGNUP (NO JWT HERE)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) throws UserException {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        
//        Cart cart = cartService.createCart(user);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(user.getRole()))
                );

        String token = jwtProvider.generateToken(auth);
        
        return new ResponseEntity<>("Signup successful. Please login.", HttpStatus.CREATED);
    }

    // SIGNIN = LOGIN (JWT CREATED HERE)
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest request) {

        Authentication authentication = authenticate(
                request.getEmail(),
                request.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse response = new AuthResponse();
        response.setJwt(token);
        response.setMessage("Login successful");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
//        return new ResponseEntity<>(
//                new AuthResponse(token, "Signup successful"),
//                HttpStatus.CREATED
//        );

    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails =
                customUserService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}

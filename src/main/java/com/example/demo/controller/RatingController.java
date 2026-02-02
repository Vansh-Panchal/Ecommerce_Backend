package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.ProductException;
import com.example.demo.exception.UserException;
import com.example.demo.model.Rating;
import com.example.demo.model.User;
import com.example.demo.request.RatingRequest;
import com.example.demo.service.RatingService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;

    public RatingController(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    private User getUserFromAuthHeader(String authHeader) throws UserException {
        String jwt = authHeader.substring(7);
        return userService.findUserProfileByJwt(jwt);
    }

    // ✅ Create rating for product
    @PostMapping
    public ResponseEntity<Rating> createRating(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RatingRequest request
    ) throws ProductException, UserException {
        User user = getUserFromAuthHeader(authHeader);
        Rating rating = ratingService.createRating(request, user);
        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }

    // ✅ Get ratings for product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getProductRatings(@PathVariable Long productId) {
        List<Rating> ratings = ratingService.getProductsRating(productId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }
}


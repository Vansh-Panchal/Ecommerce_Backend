package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.ProductException;
import com.example.demo.exception.UserException;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.request.ReviewRequest;
import com.example.demo.service.ReviewService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    private User getUserFromAuthHeader(String authHeader) throws UserException {
        String jwt = authHeader.substring(7);
        return userService.findUserProfileByJwt(jwt);
    }

    // ✅ Create review for product
    @PostMapping
    public ResponseEntity<Review> createReview(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReviewRequest request
    ) throws ProductException, UserException {
        User user = getUserFromAuthHeader(authHeader);
        Review review = reviewService.createReview(request, user);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    // ✅ Get all reviews for product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getAllReview(productId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}


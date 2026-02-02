package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/home")
@CrossOrigin
public class HomeController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public HomeController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    // Basic health/landing check
    @GetMapping
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("Ecommerce API is running", HttpStatus.OK);
    }

    // Example: featured or latest products (reuse existing list all with defaults)
    @GetMapping("/products")
    public ResponseEntity<List<Product>> listProducts() {
        // Delegate to simple category-based method; could be improved later
        List<Product> products = productService.findProductByCategory("all");
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // List all categories
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> listCategories() {
        return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
    }
}


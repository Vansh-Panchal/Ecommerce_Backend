package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.ProductException;
import com.example.demo.model.Product;
import com.example.demo.request.createProductRequest;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    // Admin: create product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody createProductRequest request)
            throws IllegalAccessException {
        Product created = productService.createProduct(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Admin: update product
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product product
    ) throws ProductException {
        Product updated = productService.updateProduct(productId, product);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Admin: delete product
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) throws ProductException {
        productService.deleteProduct(productId);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }
}


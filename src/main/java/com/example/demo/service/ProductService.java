package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.demo.exception.ProductException;
import com.example.demo.model.Product;
import com.example.demo.request.createProductRequest;

public interface ProductService {

	public Product createProduct(createProductRequest req) throws IllegalAccessException;
	
	public String deleteProduct(Long productId) throws ProductException;
	
	public Product updateProduct(Long productId, Product req) throws ProductException;
	
	public Product findProductById(Long productId) throws ProductException;
	
	public List<Product> findProductByCategory(String Category);
	
	public Page<Product> getAllProduct(String category,List<String> color, List<String> sizes, Integer minPrice, Integer maxPrice,
			Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize);
}

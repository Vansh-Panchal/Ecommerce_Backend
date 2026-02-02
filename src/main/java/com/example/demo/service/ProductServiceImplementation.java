package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ProductException;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.request.createProductRequest;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImplementation implements ProductService{

	private ProductRepository productRepository;
	private UserService userService;
	private CategoryRepository categoryRepository;
	
	
	
	public ProductServiceImplementation(ProductRepository productRepository, UserService userService,
			CategoryRepository categoryRepository) {
		// super();
		this.productRepository = productRepository;
		this.userService = userService;
		this.categoryRepository = categoryRepository;
	}

	@Transactional
	@Override
	public Product createProduct(createProductRequest req) throws IllegalAccessException {
		
		// checks if any of three level category is empty or not
		if(req.getTopLevelCategory() == null ||req.getTopLevelCategory().isBlank()) {
			throw new IllegalAccessException("Top Level Category is required");
		}
		if(req.getSecondLevelCategory() == null || req.getSecondLevelCategory().isBlank()) {
			throw new IllegalAccessException("Second Level Category is required");
		}
		if(req.getThirdLevelCategory() == null || req.getThirdLevelCategory().isBlank()) {
			throw new IllegalAccessException("Third Level Category is required");
		}
		
		
		// TopLevel Category created
		Category topLevel = categoryRepository.findByName(req.getTopLevelCategory());
		if(topLevel == null)
		{
			Category topLevelCategory = new Category();
			topLevelCategory.setName(req.getTopLevelCategory());
			topLevelCategory.setLevel(1);
			
			topLevel =categoryRepository.save(topLevelCategory); 
		}
		
		// Second Level Category created
		// inside createProduct(...)
		 // Second Level Category created
		Category secondLevel = categoryRepository.findByNameAndParent(
		        req.getSecondLevelCategory(),
		        topLevel.getName()
		);
		if (secondLevel == null) {
		    Category secondLevelCategory = new Category();
		    secondLevelCategory.setName(req.getSecondLevelCategory());
		    secondLevelCategory.setParentCategory(topLevel);
		    secondLevelCategory.setLevel(2);

		    secondLevel = categoryRepository.save(secondLevelCategory);
		}

		// Third Level Category created
		Category thirdLevel = categoryRepository.findByNameAndParent(
		        req.getThirdLevelCategory(),
		        secondLevel.getName()
		);
		if (thirdLevel == null) {
		    Category thirdLevelCategory = new Category();
		    thirdLevelCategory.setName(req.getThirdLevelCategory());
		    thirdLevelCategory.setParentCategory(secondLevel);
		    thirdLevelCategory.setLevel(3);

		    thirdLevel = categoryRepository.save(thirdLevelCategory);
		}
		
		Product product = new Product();
		product.setTitle(req.getTitle());
		product.setColor(req.getColor());
		product.setDescription(req.getDescreption());
		product.setDiscountedPrice(req.getDiscountedPrice());
		product.setDiscountPercent(req.getDiscountPercent());
		product.setImageUrl(req.getImageUrl());
		product.setBrand(req.getBrand());
		product.setPrice(req.getPrice());
		product.setSizes(req.getSize());
		product.setQuantity(req.getQuantity());
		product.setCategory(thirdLevel);
		product.setCreatedAt(LocalDateTime.now());
		
		Product savedProduct = productRepository.save(product);
		
				
		return savedProduct;
	}

	@Override
	public String deleteProduct(Long productId) throws ProductException {
		
		Product product = findProductById(productId);
		product.getSizes().clear();
		productRepository.delete(product);
		
		return "Product Delete Successfully";
	}

	@Override
	public Product updateProduct(Long productId, Product req) throws ProductException {
		
		Product product = findProductById(productId);
		if(product.getQuantity()!=0) {
			product.setQuantity(req.getQuantity());
		}
		
		
		return productRepository.save(product);
	}

	@Override
	public Product findProductById(Long productId) throws ProductException {
		
		Optional<Product> opt = productRepository.findById(productId);
		
		if(opt.isPresent())
		{
			return opt.get();
		}
		
		throw new ProductException("Product not found with id - " + productId);
	}

	@Override
	public List<Product> findProductByCategory(String Category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Product> getAllProduct(
	        String category,
	        List<String> color,
	        List<String> sizes,
	        Integer minPrice,
	        Integer maxPrice,
	        Integer minDiscount,
	        String sort,
	        String stock,
	        Integer pageNumber,
	        Integer pageSize
	) {

	    // ✅ Normalize values
		if (color != null) {
	        color = color.stream()
	                .map(String::toLowerCase)
	                .toList();
	        if (color.isEmpty()) color = null;
	    }
		System.out.println("FILTER COLORS = " + color);
	    if (minPrice == null) minPrice = 0;
	    if (maxPrice == null) maxPrice = Integer.MAX_VALUE;
	    if (minDiscount == null) minDiscount = 0;

	    Sort sortObj = Sort.by("discountedPrice");
	    if ("price_high".equals(sort)) {
	        sortObj = Sort.by("discountedPrice").descending();
	    }

	    Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);

	    return productRepository.filterProducts(
	        category,
	        color,
	        minPrice,
	        maxPrice,
	        minDiscount,
	        pageable
	    );
	}


}

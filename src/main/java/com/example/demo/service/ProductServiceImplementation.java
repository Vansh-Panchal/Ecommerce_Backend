package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ProductException;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.request.createProductRequest;
import com.example.demo.specification.ProductSpecification;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImplementation implements ProductService {

    private ProductRepository productRepository;
    private UserService userService;
    private CategoryRepository categoryRepository;

    public ProductServiceImplementation(
            ProductRepository productRepository,
            UserService userService,
            CategoryRepository categoryRepository) {
        this.productRepository  = productRepository;
        this.userService        = userService;
        this.categoryRepository = categoryRepository;
    }

    // ── Create Product ────────────────────────────────────────────────
    @Transactional
    @Override
    public Product createProduct(createProductRequest req) throws IllegalAccessException {

        if (req.getTopLevelCategory() == null || req.getTopLevelCategory().isBlank())
            throw new IllegalAccessException("Top Level Category is required");
        if (req.getSecondLevelCategory() == null || req.getSecondLevelCategory().isBlank())
            throw new IllegalAccessException("Second Level Category is required");
        if (req.getThirdLevelCategory() == null || req.getThirdLevelCategory().isBlank())
            throw new IllegalAccessException("Third Level Category is required");

        // Top level category
        Category topLevel = categoryRepository.findByName(req.getTopLevelCategory());
        if (topLevel == null) {
            Category c = new Category();
            c.setName(req.getTopLevelCategory());
            c.setLevel(1);
            topLevel = categoryRepository.save(c);
        }

        // Second level category
        Category secondLevel = categoryRepository.findByNameAndParent(
                req.getSecondLevelCategory(), topLevel.getName());
        if (secondLevel == null) {
            Category c = new Category();
            c.setName(req.getSecondLevelCategory());
            c.setParentCategory(topLevel);
            c.setLevel(2);
            secondLevel = categoryRepository.save(c);
        }

        // Third level category
        Category thirdLevel = categoryRepository.findByNameAndParent(
                req.getThirdLevelCategory(), secondLevel.getName());
        if (thirdLevel == null) {
            Category c = new Category();
            c.setName(req.getThirdLevelCategory());
            c.setParentCategory(secondLevel);
            c.setLevel(3);
            thirdLevel = categoryRepository.save(c);
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

        return productRepository.save(product);
    }

    // ── Delete Product ────────────────────────────────────────────────
    @Override
    public String deleteProduct(Long productId) throws ProductException {
        Product product = findProductById(productId);
        product.getSizes().clear();
        productRepository.delete(product);
        return "Product Deleted Successfully";
    }

    // ── Update Product ────────────────────────────────────────────────
    @Override
    public Product updateProduct(Long productId, Product req) throws ProductException {
        Product product = findProductById(productId);
        if (req.getQuantity() != 0) {
            product.setQuantity(req.getQuantity());
        }
        return productRepository.save(product);
    }

    // ── Find Product By ID ────────────────────────────────────────────
    @Override
    public Product findProductById(Long productId) throws ProductException {
        Optional<Product> opt = productRepository.findById(productId);
        if (opt.isPresent()) return opt.get();
        throw new ProductException("Product not found with id - " + productId);
    }

    // ── Find Product By Category ──────────────────────────────────────
    @Override
    public List<Product> findProductByCategory(String category) {
        return null;
    }

    // ── Find By Third Level Category ──────────────────────────────────
    @Override
    public List<Product> findByThirdLevelCategory(String thirdLevelCategory) {
        return null;
    }

    // ── Get All Products (with filters) ──────────────────────────────
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
        // ✅ Normalize colors — remove blanks, convert to lowercase
        if (color != null) {
            color = color.stream()
                    .filter(c -> c != null && !c.isBlank())
                    .map(String::toLowerCase)
                    .toList();
            if (color.isEmpty()) color = null;
        }

        System.out.println("FILTER COLORS = " + color);

        if (minPrice    == null) minPrice    = 0;
        if (maxPrice    == null) maxPrice    = Integer.MAX_VALUE;
        if (minDiscount == null) minDiscount = 0;

        // ✅ Build Specification — each predicate is independent
        // No JPQL null/collection bugs possible with this approach
        Specification<Product> spec = Specification
                .where(ProductSpecification.hasCategory(category))
                .and(ProductSpecification.hasColors(color))
                .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice))
                .and(ProductSpecification.hasMinDiscount(minDiscount));

        // ✅ Sort — unsorted by default, only applied when explicitly selected
        Sort sortObj = Sort.unsorted();
        if ("price_low".equals(sort)) {
            sortObj = Sort.by("discountedPrice").ascending();
        } else if ("price_high".equals(sort)) {
            sortObj = Sort.by("discountedPrice").descending();
        } else if ("discount".equals(sort)) {
            sortObj = Sort.by("discountPercent").descending();
        } else if ("newest".equals(sort)) {
            sortObj = Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);

        // ✅ Specification-based findAll — replaces old JPQL filterProducts query
        return productRepository.findAll(spec, pageable);
    }

    // ── Search Products ───────────────────────────────────────────────
    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();

        // Split "mens watch" → ["mens", "watch"] and search each word
        String[] words = keyword.toLowerCase().trim().split("\\s+");
        List<Product> result = new ArrayList<>();

        for (String word : words) {
            result.addAll(productRepository.searchProduct(word.trim()));
        }

        // Deduplicate by product ID — keep first occurrence
        return result.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                Product::getId,
                                p -> p,
                                (existing, duplicate) -> existing
                        ),
                        map -> new ArrayList<>(map.values())
                ));
    }
}
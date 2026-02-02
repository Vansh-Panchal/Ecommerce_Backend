package com.example.demo.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE (:category IS NULL OR p.category.name = :category)
        AND (:colors IS NULL OR LOWER(p.color) IN :colors)
        AND p.discountedPrice BETWEEN :minPrice AND :maxPrice
        AND p.discountPercent >= :minDiscount
    """)
    Page<Product> filterProducts(
            @Param("category")  String category,
            @Param("colors") List<String> colors,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("minDiscount") Integer minDiscount,
            Pageable Page
    );
}

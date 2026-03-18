package com.example.demo.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.model.Product;

// ✅ Add JpaSpecificationExecutor
public interface ProductRepository extends
        JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    @Query("""
        SELECT DISTINCT p FROM Product p
        WHERE
            LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.category.parentCategory.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.category.parentCategory.parentCategory.name)
               LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Product> searchProduct(@Param("keyword") String keyword);
}
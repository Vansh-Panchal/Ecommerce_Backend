package com.example.demo.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import com.example.demo.model.Product;
import jakarta.persistence.criteria.*;

public class ProductSpecification {

    // ✅ Category filter
    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return cb.conjunction();

            // category → parentCategory → parentCategory (3 levels deep)
            Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);

            return cb.equal(
                cb.lower(
                    cb.function("REPLACE", String.class,
                        categoryJoin.get("name"),
                        cb.literal(" "),
                        cb.literal("_")
                    )
                ),
                category.toLowerCase()
            );
        };
    }

    // ✅ Color filter — handles list properly, no Hibernate IS NULL bug
    public static Specification<Product> hasColors(List<String> colors) {
        return (root, query, cb) -> {
            if (colors == null || colors.isEmpty()) return cb.conjunction();

            // Normalize to lowercase
            List<String> lowerColors = colors.stream()
                    .map(String::toLowerCase)
                    .toList();

            // LOWER(p.color) IN (lowerColors)
            Expression<String> colorLower = cb.lower(root.get("color"));
            return colorLower.in(lowerColors);
        };
    }

    // ✅ Price range filter
    public static Specification<Product> hasPriceBetween(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return cb.conjunction();
            int minVal = (min == null) ? 0 : min;
            int maxVal = (max == null) ? Integer.MAX_VALUE : max;
            return cb.between(root.get("discountedPrice"), minVal, maxVal);
        };
    }

    // ✅ Minimum discount filter
    public static Specification<Product> hasMinDiscount(Integer minDiscount) {
        return (root, query, cb) -> {
            if (minDiscount == null || minDiscount == 0) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("discountPercent"), minDiscount);
        };
    }
}
package me.sup2is.product.repository;

import me.sup2is.product.domain.ProductCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductCategoryRepository extends CrudRepository<ProductCategory, Long> {
    List<ProductCategory> findByProductId(Long productId);
}

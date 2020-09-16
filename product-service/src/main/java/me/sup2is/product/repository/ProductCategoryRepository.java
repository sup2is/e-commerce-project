package me.sup2is.product.repository;

import me.sup2is.product.domain.ProductCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductCategoryRepository extends CrudRepository<ProductCategory, Long> {
    List<ProductCategory> findByProductId(Long productId);

    @Query("SELECT pc FROM ProductCategory pc JOIN pc.category c ON c.name IN :categoryNames")
    List<ProductCategory> findAllByCategoryNameIn(List<String> categoryNames);
}

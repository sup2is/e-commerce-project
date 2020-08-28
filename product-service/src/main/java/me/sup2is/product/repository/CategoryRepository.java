package me.sup2is.product.repository;

import me.sup2is.product.domain.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findAllByNameIn(Collection<String> categoryNames);
}

package me.sup2is.product.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.Category;
import me.sup2is.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void add(Category category) {
        categoryRepository.save(category);
    }

    public List<Category> findAllByNames(Collection<String> categoryNames) {
        for (String categoryName : categoryNames) {
            if(!categoryRepository.existsByName(categoryName))
                add(Category.createCategory(categoryName));
        }
        
        return categoryRepository.findAllByNameIn(categoryNames);
    }

}

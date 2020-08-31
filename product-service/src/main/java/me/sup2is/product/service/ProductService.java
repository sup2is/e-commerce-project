package me.sup2is.product.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.ProductCategory;
import me.sup2is.product.exception.ProductNotFoundException;
import me.sup2is.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductCategoryService productCategoryService;

    public void register(Product product, List<String> categoryNames) {
        productRepository.save(product);
        List<ProductCategory> productCategories = categoryService.findAllByNames(categoryNames).stream()
                .map(c -> ProductCategory.createProductCategory(product, c))
                .collect(Collectors.toList());
        product.classifyCategories(productCategories);
        productCategoryService.save(productCategories);
    }

    public Product findOne(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("product is not found"));
    }


}

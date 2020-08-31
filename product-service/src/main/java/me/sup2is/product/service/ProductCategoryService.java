package me.sup2is.product.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.ProductCategory;
import me.sup2is.product.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public void save(List<ProductCategory> productCategory) {
        for (ProductCategory pc : productCategory) {
            productCategoryRepository.save(pc);
        }
    }

}

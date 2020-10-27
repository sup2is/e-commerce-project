package me.sup2is.product.repository;


import me.sup2is.product.domain.Product;
import me.sup2is.product.service.ProductSearchKey;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface ProductRepositoryCustom {
    List<Product> searchAll(PageRequest pageRequest, Map<ProductSearchKey, Object> query);
}

package me.sup2is.product.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.ProductCategory;
import me.sup2is.product.domain.dto.ProductModifyDto;
import me.sup2is.product.domain.dto.ProductStockDto;
import me.sup2is.product.exception.ProductNotFoundException;
import me.sup2is.product.repository.ProductRepository;
import me.sup2is.product.web.dto.ProductStockModifyRequestDto;
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
    private final CachedProductStockService cachedProductStockService;

    public Product register(Long sellerId, Product product, List<String> categoryNames) {
        product.setSellerId(sellerId);

        productRepository.save(product);
        List<ProductCategory> productCategories = categoryService.findAllByNames(categoryNames).stream()
                .map(c -> ProductCategory.createProductCategory(product, c))
                .collect(Collectors.toList());
        product.classifyCategories(productCategories);
        productCategoryService.save(productCategories);
        cachedProductStockService.insertStock(ProductStockDto.createProductStockDto(product));
        return product;
    }

    public Product findOne(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("product is not found"));
    }

    public void modifyStock(List<ProductStockModifyRequestDto> productStockDto) {
        //todo message 기반으로 변경
        for (ProductStockModifyRequestDto stockDto : productStockDto) {
            productRepository.reduceStock(stockDto.getProductId(), stockDto.getStock());
        }
    }

    public void modify(Long sellerId, Long productId, ProductModifyDto productModifyDto)
            throws IllegalAccessException {
        Product product = findOne(productId);

        if(product.getSellerId().longValue() != sellerId.longValue()) {
            throw new IllegalAccessException("current user is not the owner of this product");
        }

        product.modify(productModifyDto);
        List<ProductCategory> productCategories = categoryService.findAllByNames(productModifyDto.getCategories()).stream()
                .map(c -> ProductCategory.createProductCategory(product, c))
                .collect(Collectors.toList());
        product.classifyCategories(productCategories);

        cachedProductStockService.evict(product.getId());
        cachedProductStockService.insertStock(ProductStockDto.createProductStockDto(product));
    }
}

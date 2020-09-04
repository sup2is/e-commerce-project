package me.sup2is.product.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.dto.ProductStockDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductStockService {

    private static final String PREFIX = "product:";
    private final HashOperations<String, String, ProductStockDto> productStockDtoHashOperations;

    public void insertStock(ProductStockDto productStockDto) {
        productStockDtoHashOperations.put(PREFIX + productStockDto.getProductId(), "product", productStockDto);
    }

}

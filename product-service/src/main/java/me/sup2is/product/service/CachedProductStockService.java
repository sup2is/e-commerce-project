package me.sup2is.product.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.dto.ProductStockDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CachedProductStockService {

    private static final String PREFIX = "product:";
    private final HashOperations<String, String, Object> hashOperations;

    public void insertStock(ProductStockDto productStockDto) {
        hashOperations.putAll(PREFIX + productStockDto.getProductId(), productStockDto.toMap());
    }
}

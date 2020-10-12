package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.sup2is.order.client.ProductServiceClient;
import me.sup2is.order.domain.dto.ProductStockDto;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.exception.ProductNotFoundException;
import me.sup2is.web.JsonResult;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CachedProductStockService {

    private final ProductServiceClient productServiceClient;
    private final HashOperations<String, String, Object> hashOperations;
    private final ObjectMapper objectMapper;
    private static final String PREFIX = "product:";

    public ProductStockDto getCachedProductStock(long productId) {
        return Optional.ofNullable(
                objectMapper.convertValue(hashOperations.get(PREFIX + productId, "entity")
                        , ProductStockDto.class)
        ).orElseGet(() -> {
            JsonResult<ProductStockDto> productStock = productServiceClient.getProductStock(productId);
            return productStock.getData();
        });
    }

    public boolean hasProductId(long productId) {
        return hashOperations.hasKey(PREFIX + productId, "stock");
    }

    public void checkItemStock(long productId, int count) {
        if(!hasProductId(productId)) {
            throw new ProductNotFoundException("product ID : " + productId + " is not found");
        }

        hashOperations.increment(PREFIX + productId, "stock", count * -1);
        int stock = (int) hashOperations.get(PREFIX + productId, "stock");
        if (stock < 0) {
            throw new OutOfStockException("product ID : " + productId + " stock is lack");
        }
    }
}

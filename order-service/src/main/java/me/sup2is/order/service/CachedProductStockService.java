package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.dto.ModifyOrderItem;
import me.sup2is.order.domain.dto.ProductStockDto;
import me.sup2is.order.exception.OutOfStockException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CachedProductStockService {

    private final HashOperations<String, String, ProductStockDto> productStockDtoHashOperations;
    private final ObjectMapper objectMapper;
    private static final String PREFIX = "product:";

    public ProductStockDto getCachedProductStock(long productId) {
        return  Optional.ofNullable(
                objectMapper.convertValue(productStockDtoHashOperations.get(PREFIX + productId,
                        "product"),
                        ProductStockDto.class))
                .orElseThrow(() -> new IllegalArgumentException());
        //todo orElse 부분을 product service client랑 연결해야됨 레디스 장애시 서비스에 의존하도록
    }

}

package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.sup2is.order.domain.OrderItem;
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

    public void checkItemStock(OrderItem orderItem) throws OutOfStockException {

        ProductStockDto productStockDto =
                Optional.ofNullable(
                        objectMapper.convertValue(productStockDtoHashOperations.get(PREFIX + orderItem.getProductId(),
                                "product"),
                            ProductStockDto.class))
                        .orElseThrow(() -> new IllegalArgumentException());

        if(productStockDto.getStock() < orderItem.getCount())
            throw new OutOfStockException("product ID : " + orderItem.getProductId() + " stock is lack");

    }

}

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

    public void checkItemStock(OrderItem orderItem) throws OutOfStockException {
        if(haveItemStock(orderItem.getProductId(), orderItem.getCount()))
            throw new OutOfStockException("product ID : " + orderItem.getProductId() + " stock is lack");
    }

    public void checkItemStock(ModifyOrderItem modifyOrderItem) throws OutOfStockException {
        if(haveItemStock(modifyOrderItem.getProductId(), modifyOrderItem.getCount()))
            throw new OutOfStockException("product ID : " + modifyOrderItem.getProductId() + " stock is lack");
    }

    private boolean haveItemStock(long productId, int count) {
        ProductStockDto productStockDto =
                Optional.ofNullable(
                        objectMapper.convertValue(productStockDtoHashOperations.get(PREFIX + productId,
                                "product"),
                                ProductStockDto.class))
                        .orElseThrow(() -> new IllegalArgumentException());
        //todo orElse 부분을 product service client랑 연결해야됨 레디스 장애시 서비스에 의존하도록
        return productStockDto.getStock() < count;
    }

}

package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.dto.ProductStockDto;
import me.sup2is.order.exception.OutOfStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.HashOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = CachedProductStockService.class)
@Import(ObjectMapper.class)
class CachedProductStockServiceTest {

    @MockBean
    HashOperations<String, String, ProductStockDto> productStockDtoHashOperations;

    @Autowired
    CachedProductStockService cachedProductStockService;

    @Test
    @DisplayName("상품 재고를 redis에서 체크")
    public void check_item_stock() {
        //given
        ProductStockDto productStockDto = new ProductStockDto(1L, 5, 5000L);

        Mockito.when(productStockDtoHashOperations.get("product:" + productStockDto.getProductId(),
                "product")).thenReturn(productStockDto);

        //when
        //then
        ProductStockDto cachedProductStock = cachedProductStockService.getCachedProductStock(1L);
        assertEquals(productStockDto.getProductId(), cachedProductStock.getProductId());
        assertEquals(productStockDto.getStock(), cachedProductStock.getStock());
        assertEquals(productStockDto.getPrice(), cachedProductStock.getPrice());

    }
}
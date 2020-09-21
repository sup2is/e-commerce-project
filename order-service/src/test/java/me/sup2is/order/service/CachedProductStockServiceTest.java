package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    HashOperations<String, String, Object> hashOperations;

    @Autowired
    CachedProductStockService cachedProductStockService;

    @Test
    @DisplayName("상품 재고를 redis에서 체크하고 재고가 없을때")
    public void check_item_stock() {
        //given
        ProductStockDto productStockDto = new ProductStockDto(1L, 5, 5000L);

        Mockito.when(hashOperations.get("product:" + productStockDto.getProductId(),
                "stock")).thenReturn(-1);

        //when
        //then
        assertThrows(OutOfStockException.class, () -> cachedProductStockService.checkItemStock(productStockDto.getProductId(), 6));
    }

}
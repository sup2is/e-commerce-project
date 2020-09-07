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
    public void check_item_stock() throws OutOfStockException {
        //given
        OrderItem.Builder itemBuilder = new OrderItem.Builder();
        itemBuilder.productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(2);
        OrderItem orderItem = OrderItem.createOrderItem(itemBuilder);

        ProductStockDto productStockDto = new ProductStockDto(1L, 5);

        Mockito.when(productStockDtoHashOperations.get("product:" + productStockDto.getProductId(),
                "product")).thenReturn(productStockDto);

        //when
        //then
        cachedProductStockService.checkItemStock(orderItem);

    }

    @Test
    @DisplayName("상품 재고를 redis에서 체크 & 재고 부족으로 OutOfStockException()")
    public void lacked_item_stock() {
        //given
        OrderItem.Builder itemBuilder = new OrderItem.Builder();
        itemBuilder.productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(2);
        OrderItem orderItem = OrderItem.createOrderItem(itemBuilder);

        ProductStockDto productStockDto = new ProductStockDto(1L, 1);

        Mockito.when(productStockDtoHashOperations.get("product:" + productStockDto.getProductId(),
                "product")).thenReturn(productStockDto);

        //when
        //then
        assertThrows(OutOfStockException.class ,
                () -> cachedProductStockService.checkItemStock(orderItem));
    }

    @Test
    @DisplayName("상품 재고를 redis에서 체크 & redis에 해당 key가 없을때 IllegalArgumentException()")
    public void product_id_not_found() {
        //given
        OrderItem.Builder itemBuilder = new OrderItem.Builder();
        itemBuilder.productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(2);
        OrderItem orderItem = OrderItem.createOrderItem(itemBuilder);

        ProductStockDto productStockDto = new ProductStockDto(1L, 1);

        Mockito.when(productStockDtoHashOperations.get("product:" + productStockDto.getProductId(),
                "product")).thenReturn(null);

        //when
        //then
        assertThrows(IllegalArgumentException.class ,
                () -> cachedProductStockService.checkItemStock(orderItem));
    }

}
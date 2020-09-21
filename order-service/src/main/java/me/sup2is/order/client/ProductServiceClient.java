package me.sup2is.order.client;

import me.sup2is.order.web.dto.ProductStockModifyRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(value = "product-service", primary = false)
public interface ProductServiceClient {

    @PutMapping("/stock")
    void modifyStock(List<ProductStockModifyRequestDto> dtoByOrderItems);
}

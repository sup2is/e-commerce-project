package me.sup2is.order.client;

import me.sup2is.order.domain.dto.ProductStockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;

@FeignClient(value = "product-service", primary = false)
public interface ProductServiceClient {

    @PatchMapping("/")
    void modifyStock(List<ProductStockDto> dtoByOrderItems);
}

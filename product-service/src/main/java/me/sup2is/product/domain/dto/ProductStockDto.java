package me.sup2is.product.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.product.domain.Product;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ProductStockDto implements Serializable {

    private long productId;
    private int stock;
    private long price;

    public static ProductStockDto createProductStockDto(Product product) {
        return new ProductStockDto(product.getId(), product.getStock(), product.getPrice());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("stock", stock);
        map.put("entity", this);
        return map;
    }

}

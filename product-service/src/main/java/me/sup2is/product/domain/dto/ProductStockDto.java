package me.sup2is.product.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ProductStockDto implements Serializable {

    private long productId;
    private int stock;
    private long price;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("stock", stock);
        map.put("entity", this);
        return map;
    }

}

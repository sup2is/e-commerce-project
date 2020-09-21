package me.sup2is.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductStockModifyRequestDto {

    private long productId;
    private int stock;

}

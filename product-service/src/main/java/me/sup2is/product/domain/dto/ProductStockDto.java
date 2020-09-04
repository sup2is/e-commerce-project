package me.sup2is.product.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ProductStockDto implements Serializable {

    private long productId;
    private int stock;

}

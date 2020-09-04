package me.sup2is.order.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockDto implements Serializable {

    private long productId;
    private int stock;

}

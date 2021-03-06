package me.sup2is.order.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sup2is.order.domain.OrderItem;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockDto implements Serializable {

    private long productId;
    private int stock;
    private long price;

}

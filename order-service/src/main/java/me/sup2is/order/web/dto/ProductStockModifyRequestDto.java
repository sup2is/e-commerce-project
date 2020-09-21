package me.sup2is.order.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.order.domain.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProductStockModifyRequestDto {

    private long productId;
    private int stock;

    public static List<ProductStockModifyRequestDto> createDtoByOrderItems(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(o -> new ProductStockModifyRequestDto(o.getProductId(), o.getCount() * -1))
                .collect(Collectors.toList());
    }

}

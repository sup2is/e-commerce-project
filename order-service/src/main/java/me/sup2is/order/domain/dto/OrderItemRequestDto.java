package me.sup2is.order.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.order.domain.OrderItem;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class OrderItemRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    private Long price;

    @NotNull
    private Integer count;

    private Integer discountRate;

    public OrderItem toEntity() {
        OrderItem.Builder builder = new OrderItem.Builder();
        builder.count(count)
                .discountRate(discountRate)
                .price(price)
                .productId(productId);
        return OrderItem.createOrderItem(builder);
    }

}
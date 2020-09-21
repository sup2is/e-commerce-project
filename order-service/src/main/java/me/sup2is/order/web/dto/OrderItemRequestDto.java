package me.sup2is.order.web.dto;

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
    private Integer count;

    private Integer discountRate;

    public OrderItem toEntity() {
        return OrderItem.Builder.builder()
                .count(count)
                .discountRate(0) //todo 미구현
                .productId(productId)
                .build()
                .toEntity();
    }

}

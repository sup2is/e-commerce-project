package me.sup2is.order.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ModifyOrderItemRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    private Integer count;

    private Integer discountRate;

    public static List<ModifyOrderItem> toModifyOrderItems(List<ModifyOrderItemRequestDto> orderItemRequestDtos) {
        return orderItemRequestDtos.stream()
                .map(obj -> new ModifyOrderItem(obj.getProductId(), obj.getCount()))
                .collect(Collectors.toList());
    }

}

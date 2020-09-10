package me.sup2is.order.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyOrderItem {
    private Long productId;
    private Integer count;
}
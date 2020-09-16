package me.sup2is.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PriceQueryDto {

    private String operation;
    private Long value;

}

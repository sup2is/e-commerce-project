package me.sup2is.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductModifyDto {

    private String name;

    private String code;

    private String brandName;

    private String description;

    private int stock;

    private long price;

    private boolean salable;

    private List<String> categories;

}

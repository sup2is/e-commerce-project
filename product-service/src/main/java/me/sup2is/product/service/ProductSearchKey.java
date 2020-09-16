package me.sup2is.product.service;

import lombok.Getter;

@Getter
public enum ProductSearchKey {

    NAME("name"),
    CODE("code"),
    BRAND_NAME("brand_name"),
    PRICE("price"),
    CATEGORY("product_id");

    private String name;

    ProductSearchKey(String name) {
        this.name = name;
    }
}
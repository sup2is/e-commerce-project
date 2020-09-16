package me.sup2is.product.service;

import lombok.Getter;

@Getter
public enum ProductSearchKey {

    NAME("name"),
    CODE("code"),
    BRAND_NAME("brandName"),
    MAX_PRICE("price"),
    MIN_PRICE("price"),
    CATEGORY("category");

    private String name;

    ProductSearchKey(String name) {
        this.name = name;
    }
}
package me.sup2is.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.product.domain.Product;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductRequestDto {

    private Long sellerId;

    private String name;

    private String code;

    private String brandName;

    private String description;

    private int stock;

    private Long price;

    private boolean salable;

    private List<String> categories;

    public Product toEntity() {
        Product.Builder builder = new Product.Builder();
        builder.setCode(this.code)
                .setDescription(this.description)
                .setBrandName(this.brandName)
                .setName(this.name)
                .setPrice(this.price)
                .setSalable(this.salable)
                .setSellerId(this.sellerId)
                .setStock(this.stock);
        return Product.createProduct(builder);
    }
}

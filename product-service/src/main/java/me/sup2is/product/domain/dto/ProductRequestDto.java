package me.sup2is.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.sup2is.product.domain.Product;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotNull
    private String name;

    @NotNull
    private String code;

    @NotNull
    private String brandName;

    @NotNull
    private String description;

    @NotNull
    private Integer stock;

    @NotNull
    private Long price;

    @NotNull
    private Boolean salable;

    @NotEmpty
    private List<String> categories;

    public Product toEntity() {
        Product.Builder builder = new Product.Builder();
        builder.setCode(this.code)
                .setDescription(this.description)
                .setBrandName(this.brandName)
                .setName(this.name)
                .setPrice(this.price)
                .setSalable(this.salable)
                .setStock(this.stock);
        return Product.createProduct(builder);
    }
}

package me.sup2is.product.web.dto;

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
        return Product.Builder.builder()
                .code(this.code)
                .description(this.description)
                .brandName(this.brandName)
                .name(this.name)
                .price(this.price)
                .salable(this.salable)
                .stock(this.stock)
                .build()
                .toEntity();
    }
}

package me.sup2is.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductModifyRequestDto {

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

    public ProductModifyDto toProductModifyDto() {
        return ProductModifyDto.builder()
                .brandName(this.brandName)
                .categories(this.categories)
                .code(this.code)
                .description(this.description)
                .name(this.name)
                .price(this.price)
                .salable(this.salable)
                .stock(this.stock)
                .build();
    }
}

package me.sup2is.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.sup2is.product.domain.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;

    private Long sellerId;

    private String name;

    private String code;

    private String brandName;

    private String description;

    private Integer stock;

    private Long price;

    private List<String> categories = new ArrayList<>();

    private boolean salable;

    private LocalDateTime createAt;

    private LocalDateTime updatedAt;

    public ProductResponseDto(Product findProduct) {
        this.id = findProduct.getId();
        this.sellerId = findProduct.getSellerId();
        this.createAt = findProduct.getCreateAt();
        this.updatedAt = findProduct.getUpdatedAt();
        this.name = findProduct.getName();
        this.code = findProduct.getCode();
        this.brandName = findProduct.getBrandName();
        this.description = findProduct.getDescription();
        this.stock = findProduct.getStock();
        this.price = findProduct.getPrice();
        this.salable = findProduct.isSalable();
        this.categories = findProduct.getCategories().stream()
                .map(cp -> cp.getCategory().getName())
                .collect(toList());
    }

    public static List<ProductResponseDto> createProductsResponseDto(List<Product> findProducts) {
        return findProducts.stream()
                .map(ProductResponseDto::new)
                .collect(toList());
    }

}

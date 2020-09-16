package me.sup2is.product.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sup2is.product.domain.audit.AuditTime;
import me.sup2is.product.domain.dto.ProductModifyDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends AuditTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private Long sellerId;

    private String name;

    private String code;

    private String brandName;

    private String description;

    private Integer stock;

    private Long price;

    @OneToMany(mappedBy = "product")
    private List<ProductCategory> categories = new ArrayList<>();

    private boolean salable;

    public static Product createProduct(Builder builder) {
        Product product = new Product();
        product.categories = builder.categories;
        product.description = builder.description;
        product.brandName = builder.brandName;
        product.name = builder.name;
        product.code = builder.code;
        product.price = builder.price;
        product.salable = builder.salable;
        product.stock = builder.stock;
        return product;
    }

    public void classifyCategories(List<ProductCategory> categories) {
        this.categories = categories;
    }

    public void modifyStock(int stock) {
        this.stock += stock;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public void modify(ProductModifyDto productModifyDto) {
        this.stock = productModifyDto.getStock();
        this.brandName = productModifyDto.getBrandName();
        this.price = productModifyDto.getPrice();
        this.description = productModifyDto.getDescription();
        this.name = productModifyDto.getName();
        this.salable = productModifyDto.isSalable();
        this.code = productModifyDto.getCode();
    }

    @lombok.Builder
    public static class Builder {
        private String name;
        private String code;
        private String brandName;
        private String description;
        private Integer stock;
        private Long price;
        private List<ProductCategory> categories;
        private Boolean salable;

        public Product toEntity() {
            return Product.createProduct(this);
        }
    }

}

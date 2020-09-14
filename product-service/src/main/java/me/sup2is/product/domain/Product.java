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
        return builder.build();
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

    public static class Builder {
        private Long sellerId;

        private String name;

        private String code;

        private String brandName;

        private String description;

        private Integer stock;

        private Long price;

        private List<ProductCategory> categories = new ArrayList<>();

        private Boolean salable;

        public Builder setSellerId(Long sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setBrandName(String brandName) {
            this.brandName = brandName;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setStock(Integer stock) {
            this.stock = stock;
            return this;
        }

        public Builder setPrice(Long price) {
            this.price = price;
            return this;
        }

        public Builder setSalable(boolean salable) {
            this.salable = salable;
            return this;
        }

        private Product build() {
            Product product = new Product();
            product.categories = this.categories;
            product.description = this.description;
            product.sellerId = this.sellerId;
            product.brandName = this.brandName;
            product.name = this.name;
            product.code = this.code;
            product.price = this.price;
            product.salable = this.salable;
            product.stock = this.stock;
            return product;
        }
    }

}

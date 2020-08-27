package me.sup2is.product.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long id;

    private Long sellerId;

    private String name;

    private String description;

    private int stock;

    private BigDecimal price;

    @OneToMany(mappedBy = "product")
    private List<ProductCategory> categories = new ArrayList<>();

    private boolean salable;

}

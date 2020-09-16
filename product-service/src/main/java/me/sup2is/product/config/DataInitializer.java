package me.sup2is.product.config;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.Product;
import me.sup2is.product.service.ProductService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ProductService productService;

    @PostConstruct
    public void init() {

        for (int i = 0; i < 100; i++) {

            Product product = Product.Builder.builder()
                    .brandName("brand" + i)
                    .code("AA" + i)
                    .description("description" + i)
                    .name("product name " + i)
                    .salable(true)
                    .price(10000L)
                    .stock((int) (Math.random() * 100))
                    .build()
                    .toEntity();

            productService.register(1L, product, Arrays.asList((int) (Math.random() * 10) + ""));
        }
    }
    
}

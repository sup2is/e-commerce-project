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
            Product.Builder builder = new Product.Builder();
            Product product = Product.createProduct(builder.setBrandName("brand" + i)
                    .setCode("AA" + i)
                    .setDescription("description" + i)
                    .setName("product name " + i)
                    .setPrice(10000L)
                    .setSalable(true)
                    .setSellerId((long) i)
                    .setStock((int) (Math.random() * 100)));

            productService.register(product, Arrays.asList((int) (Math.random() * 10) + ""));
        }
    }
    
}

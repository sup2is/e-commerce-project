package me.sup2is.product.repository;

import me.sup2is.product.config.QueryDSLConfiguration;
import me.sup2is.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@EnableJpaAuditing
@Import(QueryDSLConfiguration.class)
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    public void save_without_categories() {

        //given
        Product product = Product.Builder.builder()
                .price(10000L)
                .salable(true)
                .name("청바지")
                .brandName("리바이스")
                .description("빈티지")
                .code("AA123")
                .build()
                .toEntity();

        productRepository.save(product);

        //then
        assertEquals(product, productRepository.findById(product.getId()).get());
    }

}
package me.sup2is.product.repository;

import me.sup2is.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    public void save_without_categories() {

        //given
        Product.Builder builder = new Product.Builder();
        builder.setStock(5)
                .setSellerId(1L)
                .setPrice(10000L)
                .setSalable(true)
                .setName("청바지")
                .setBrandName("리바이스")
                .setDescription("빈티지")
                .setCode("AA123");

        Product product = Product.createProduct(builder);

        //when
        productRepository.save(product);

        //then
        assertEquals(product, productRepository.findById(product.getId()).get());
    }

}
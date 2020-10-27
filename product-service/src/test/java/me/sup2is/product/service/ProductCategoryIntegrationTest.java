package me.sup2is.product.service;

import me.sup2is.product.client.MemberServiceClient;
import me.sup2is.product.config.QueryDSLConfiguration;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.ProductCategory;
import me.sup2is.product.repository.ProductCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({ProductService.class,
        ProductCategoryService.class,
        CategoryService.class,
        CachedProductStockService.class,
        QueryDSLConfiguration.class})
@Transactional
@EnableJpaAuditing
public class ProductCategoryIntegrationTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductCategoryService productCategoryService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @MockBean(name = "memberServiceClient")
    MemberServiceClient memberServiceClient;

    @MockBean
    HashOperations<String, String, Object> hashOperations;

    @Test
    @DisplayName("Category, Product N:M 테스트")
    public void register() {
        //given
        List<String> categoryNames = Arrays.asList("의류", "청바지");

        Product product = Product.Builder.builder()
                .name("청바지")
                .code("AA123")
                .brandName("리바이스")
                .description("빈티지")
                .stock(5)
                .price(10000L)
                .salable(true)
                .build()
                .toEntity();

        //when

        productService.register(1L, product, categoryNames);

        //then
        Product one = productService.findOne(product.getId());
        assertEquals(product, one);

        List<ProductCategory> productCategories = product.getCategories();
        List<ProductCategory> byProductId = productCategoryRepository.findByProductId(product.getId());
        assertEquals(productCategories.get(0), byProductId.get(0));
        assertEquals(productCategories.get(1), byProductId.get(1));
        assertEquals(productCategories.size(), byProductId.size());

    }


}

package me.sup2is.product.service;

import me.sup2is.product.client.MemberServiceClient;
import me.sup2is.product.domain.Category;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.ProductCategory;
import me.sup2is.product.domain.dto.MemberDto;
import me.sup2is.product.domain.dto.ProductStockDto;
import me.sup2is.product.web.dto.ProductRequestDto;
import me.sup2is.product.repository.ProductCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
@Transactional
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
    HashOperations<String, String, Object> stringStringProductStockDtoHashOperations;

    @MockBean
    HashOperations<String, String, MemberDto> stringStringMemberDtoHashOperations;

    @Test
    @DisplayName("Category, Product N:M 테스트")
    public void register() {
        //given
        List<String> categoryNames = Arrays.asList("의류", "청바지");
        Category category1 = Category.createCategory("의류");
        Category category2 = Category.createCategory("청바지");
        List<Category> categories = Arrays.asList(category1,
                category2);

        ProductRequestDto productRequestDto = new ProductRequestDto(
                "청바지",
                "AA123",
                "리바이스",
                "빈티지",
                5,
                10000L,
                true,
                categoryNames);

        Product product = productRequestDto.toEntity();
        //when

        for (Category category : categories) {
            categoryService.add(category);
        }

        productService.register(1L, product, productRequestDto.getCategories());

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

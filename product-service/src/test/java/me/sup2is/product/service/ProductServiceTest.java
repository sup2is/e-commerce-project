package me.sup2is.product.service;

import me.sup2is.product.domain.Category;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.dto.ProductRequestDto;
import me.sup2is.product.domain.dto.ProductStockDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.HashOperations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({ProductService.class, ProductCategoryService.class, ProductStockService.class})
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @MockBean
    CategoryService categoryService;

    @MockBean
    HashOperations<String, String, ProductStockDto> productStockDtoHashOperations;

    @Test
    public void register_with_categories(){
        //given
        List<String> categoryNames = Arrays.asList("의류", "청바지");
        Category category1 = Category.createCategory("의류");
        Category category2 = Category.createCategory("청바지");
        List<Category> categories = Arrays.asList(category1,
                category2);

        ProductRequestDto productRequestDto = new ProductRequestDto(1L,
                "청바지",
                "AA123",
                "리바이스",
                "빈티지",
                5,
                10000L,
                true,
                categoryNames);

        Product product = productRequestDto.toEntity();
        Mockito.when(categoryService.findAllByNames(categoryNames)).thenReturn(categories);

        //when
        productService.register(product, productRequestDto.getCategories());
        Product findProduct = productService.findOne(product.getId());

        //then
        assertEquals(categories.size(), findProduct.getCategories().size());
        assertEquals(category1.getName(), findProduct.getCategories().get(0).getCategory().getName());
        assertEquals(category2.getName(), findProduct.getCategories().get(1).getCategory().getName());

    }


}
package me.sup2is.product.service;

import me.sup2is.product.domain.Category;
import me.sup2is.product.domain.Product;
import me.sup2is.product.web.dto.ProductModifyRequestDto;
import me.sup2is.product.web.dto.ProductStockModifyRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.core.HashOperations;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;

@DataJpaTest
@Import({ProductService.class, ProductCategoryService.class, CachedProductStockService.class})
@EnableJpaAuditing
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @MockBean
    CategoryService categoryService;

    @MockBean
    HashOperations<String, String, Object> hashOperations;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ProductCategoryService productCategoryService;

    @Test
    public void register_with_categories(){
        //given
        List<String> categoryNames = Arrays.asList("의류", "청바지");
        Product product = getProduct();

        Category category1 = Category.createCategory("의류");
        Category category2 = Category.createCategory("청바지");
        List<Category> categories = Arrays.asList(category1,
                category2);

        Mockito.when(categoryService.findAllByNamesAndInsertNotExist(anyList()))
                .thenReturn(categories);


        //when
        productService.register(1L, product, categoryNames);
        Product findProduct = productService.findOne(product.getId());

        //then
        assertEquals(categories.size(), findProduct.getCategories().size());
        assertEquals(category1.getName(), findProduct.getCategories().get(0).getCategory().getName());
        assertEquals(category2.getName(), findProduct.getCategories().get(1).getCategory().getName());

    }

    @Test
    public void modify_stock() {
        //given
        List<String> categoryNames = Arrays.asList("의류", "청바지");

        Product product = getProduct();
        productService.register(1L, product, categoryNames);

        //when
        ProductStockModifyRequestDto productStockDto = new ProductStockModifyRequestDto(product.getId(), -2);
        productService.modifyStock(Arrays.asList(productStockDto));

        //then
        entityManager.clear();
        Product one = productService.findOne(product.getId());
        assertEquals(3, one.getStock());

    }

    @Test
    public void modify() throws IllegalAccessException {
        //given
        List<String> categoryNames = Arrays.asList("의류", "청바지");
        Product product = getProduct();

        Category category1 = Category.createCategory("의류");
        Category category2 = Category.createCategory("청바지");
        List<Category> categories = Arrays.asList(category1,
                category2);

        Mockito.when(categoryService.findAllByNamesAndInsertNotExist(anyList()))
                .thenReturn(categories);

        productService.register(1L, product, categoryNames);

        ProductModifyRequestDto productModifyRequestDto = ProductModifyRequestDto.builder()
                .brandName("캘빈클라인")
                .code("BB12345")
                .description("빈티지상품")
                .name("청바지")
                .price(50000L)
                .salable(false)
                .stock(20)
                .categories(Arrays.asList("의류"))
                .build();



        List<Category> modifyCategories = Arrays.asList(category1);
        Mockito.when(categoryService.findAllByNamesAndInsertNotExist(anyList()))
                .thenReturn(modifyCategories);

        //when
        productService.modify(1L, 1L, productModifyRequestDto.toProductModifyDto());

        //then

        Product findProduct = productService.findOne(1L);

        assertEquals(productModifyRequestDto.getBrandName(), findProduct.getBrandName());
        assertEquals(productModifyRequestDto.getCode(), findProduct.getCode());
        assertEquals(productModifyRequestDto.getDescription(), findProduct.getDescription());
        assertEquals(productModifyRequestDto.getName(), findProduct.getName());
        assertEquals(productModifyRequestDto.getPrice(), findProduct.getPrice());
        assertEquals(productModifyRequestDto.getCategories().size(), findProduct.getCategories().size());
        assertEquals(productModifyRequestDto.getSalable(), findProduct.isSalable());

    }

    private Product getProduct() {
        return  Product.Builder.builder()
                .name("청바지")
                .code("AA123")
                .brandName("리바이스")
                .description("빈티지")
                .stock(5)
                .price(10000L)
                .salable(true)
                .build()
                .toEntity();
    }


}
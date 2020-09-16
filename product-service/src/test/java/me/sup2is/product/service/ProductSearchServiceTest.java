package me.sup2is.product.service;

import me.sup2is.product.domain.Product;
import me.sup2is.product.web.dto.ProductSpecificationQueryDto;
import me.sup2is.product.repository.ProductRepository;
import me.sup2is.product.web.dto.ProductPageRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ProductCategoryService.class,
        ProductSearchService.class,
        ProductService.class,
        CategoryService.class})
@EnableJpaAuditing
@Transactional
class ProductSearchServiceTest {

    @Autowired
    ProductSearchService productSearchService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @MockBean
    ProductStockService productStockService;

    @Test
    @DisplayName("상품명으로 검색")
    public void find_all_by_query_with_name() {
        //given

        Product product = getProduct(10000L, "청바지", "리바이스", "빈티지", "AA123");
        Product product2 = getProduct(50000L, "면바지", "캘빈클라인", "음 ...", "CK123");

        productRepository.save(product);
        productRepository.save(product2);

        ProductSpecificationQueryDto productSpecificationQuery =
                new ProductSpecificationQueryDto("바지", "", "", 0L, 0L, null);
        PageRequest productPageRequest = ProductPageRequestDto.createProductPageRequest(0, 5);

        //when
        List<Product> allByQuery = productSearchService.findAllByQuery(productPageRequest, productSpecificationQuery.createQueryMap());

        //then
        System.out.println(allByQuery);
    }

    @Test
    @DisplayName("카테고리명 + 상품명으로 검색")
    public void find_all_by_query_with_category_and_product_name() {
        //given
        Product product = getProduct(10000L, "청바지", "리바이스", "빈티지", "AA123");
        Product product2 = getProduct(50000L, "면바지", "캘빈클라인", "음 ...", "CK123");

        productService.register(1L, product, Arrays.asList("의류"));
        productService.register(1L, product2, Arrays.asList("의류", "바지"));

        ProductSpecificationQueryDto productSpecificationQuery = new ProductSpecificationQueryDto("바지",
                "",
                "",
                0L,
                0L,
                Arrays.asList("바지"));
        PageRequest productPageRequest = ProductPageRequestDto.createProductPageRequest(0, 5);

        //when
        List<Product> allByQuery = productSearchService.findAllByQuery(productPageRequest, productSpecificationQuery.createQueryMap());

        //then
        assertEquals(1, allByQuery.size());

        Product one = productService.findOne(product2.getId());
        assertEquals(one, allByQuery.get(0));
    }

    @Test
    @DisplayName("카테고리명 + 상품명 + 가격으로 검색")
    public void find_all_by_query_with_category_and_product_name_and_price() {
        //given
        Product product = getProduct(10000L, "청바지", "리바이스", "빈티지", "AA123");
        Product product2 = getProduct(50000L, "면바지", "캘빈클라인", "음 ...", "CK123");
        Product product3 = getProduct(70000L, "정장바지", "갤럭시", "음 ...", "BB123");

        productService.register(1L, product, Arrays.asList("의류"));
        productService.register(1L, product2, Arrays.asList("의류", "바지"));
        productService.register(1L, product3, Arrays.asList("의류", "정장"));

        ProductSpecificationQueryDto productSpecificationQuery = new ProductSpecificationQueryDto("바지",
                "",
                "",
                52000L,
                80000L,
                Arrays.asList("의류", "정장"));
        PageRequest productPageRequest = ProductPageRequestDto.createProductPageRequest(0, 5);

        //when
        List<Product> allByQuery = productSearchService.findAllByQuery(productPageRequest, productSpecificationQuery.createQueryMap());

        //then
        assertEquals(1, allByQuery.size());

        Product one = productService.findOne(product3.getId());
        assertEquals(one, allByQuery.get(0));
    }

    private Product getProduct(long price, String name, String brandName, String description, String code) {
        return Product.Builder.builder()
                .stock(5)
                .price(price)
                .salable(true)
                .name(name)
                .brandName(brandName)
                .description(description)
                .code(code)
                .build()
                .toEntity();
    }
}
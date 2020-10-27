package me.sup2is.product.service;

import me.sup2is.product.config.QueryDSLConfiguration;
import me.sup2is.product.domain.Product;
import me.sup2is.product.web.dto.ProductSpecificationQueryDto;
import me.sup2is.product.repository.ProductRepository;
import me.sup2is.product.web.dto.ProductPageRequestDto;
import org.junit.jupiter.api.BeforeEach;
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
        ProductService.class,
        CategoryService.class,
        QueryDSLConfiguration.class})
@EnableJpaAuditing
@Transactional
class ProductSearchServiceTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @MockBean
    CachedProductStockService cachedProductStockService;

    @BeforeEach
    public void setup() {
        Product product = getProduct(10000L, "청바지", "리바이스", "빈티지", "AA123");
        Product product2 = getProduct(50000L, "면바지", "캘빈클라인", "bla bla", "CK123");
        Product product3 = getProduct(70000L, "티셔츠", "지오다노", "bla bla", "ZZ125");
        Product product4 = getProduct(100000L, "아우터", "자라", "bla bla", "ZR125");
        Product product5 = getProduct(30000L, "모자", "캉골", "bla bla", "KG125");

        productService.register(1L, product, Arrays.asList("의류", "청바지"));
        productService.register(1L, product2, Arrays.asList("의류", "면바지"));
        productService.register(1L, product3, Arrays.asList("의류", "티셔츠"));
        productService.register(1L, product4, Arrays.asList("의류", "자켓"));
        productService.register(1L, product5, Arrays.asList("악세서리", "모자"));

    }

    @Test
    @DisplayName("상품명으로 검색")
    public void find_all_by_query_with_name() {
        //given
        ProductSpecificationQueryDto productSpecificationQuery =
                new ProductSpecificationQueryDto("바지"
                        , null
                        , null
                        , null
                        , null
                        , null);
        PageRequest productPageRequest = ProductPageRequestDto.createProductPageRequest(0, 5);

        //when
        List<Product> allByQuery = productService.findAllByQuery(productPageRequest, productSpecificationQuery.createQueryMap());

        //then
        assertEquals(2, allByQuery.size());
    }

    @Test
    @DisplayName("카테고리명으로 검색")
    public void find_all_by_query_with_category_name() {
        //given
        ProductSpecificationQueryDto productSpecificationQuery =
                new ProductSpecificationQueryDto(null
                        , null
                        , null
                        , null
                        , null
                        , Arrays.asList("의류"));
        PageRequest productPageRequest = ProductPageRequestDto.createProductPageRequest(0, 5);

        //when
        List<Product> allByQuery = productService.findAllByQuery(productPageRequest, productSpecificationQuery.createQueryMap());

        //then
        assertEquals(4, allByQuery.size());
    }

    @Test
    @DisplayName("카테고리명 + 상품명으로 검색")
    public void find_all_by_query_with_category_and_product_name() {
        //given
        ProductSpecificationQueryDto productSpecificationQuery =
                new ProductSpecificationQueryDto(
                        "바지",
                        null,
                        null,
                        null,
                        null,
                Arrays.asList("의류"));
        PageRequest productPageRequest = ProductPageRequestDto.createProductPageRequest(0, 5);

        //when
        List<Product> allByQuery = productService.findAllByQuery(productPageRequest, productSpecificationQuery.createQueryMap());

        //then
        assertEquals(2, allByQuery.size());
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
        List<Product> allByQuery = productService.findAllByQuery(productPageRequest, productSpecificationQuery.createQueryMap());

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
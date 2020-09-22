package me.sup2is.product.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.product.config.MockTestConfiguration;
import me.sup2is.product.config.RestDocsConfiguration;
import me.sup2is.product.domain.Category;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.ProductCategory;
import me.sup2is.product.domain.dto.MemberDto;
import me.sup2is.product.service.ProductSearchKey;
import me.sup2is.product.service.ProductSearchService;
import me.sup2is.product.web.dto.*;
import me.sup2is.product.service.MemberService;
import me.sup2is.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
@Import({JwtTokenUtil.class, RestDocsConfiguration.class, MockTestConfiguration.class})
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @MockBean
    ProductSearchService productSearchService;

    @Test
    public void register() throws Exception {
        //given
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .brandName("리바이스")
                .categories(Arrays.asList("의류", "청바지"))
                .code("AA123")
                .description("빈티지")
                .name("청바지")
                .price(10000L)
                .salable(true)
                .stock(1000)
                .build();

        doAnswer(i -> null)
                .when(productService)
                .register(1L, productRequestDto.toEntity(), productRequestDto.getCategories());

        String email = "test@example.com";
        MemberDto memberDto = getMemberDto(email);

        Mockito.when(memberService.getMember(email))
                .thenReturn(memberDto);

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/")
                    .content(objectMapper.writeValueAsString(productRequestDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("create-product",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("상품명"),
                                fieldWithPath("code").description("상품 고유 코드"),
                                fieldWithPath("brandName").description("브랜드 명"),
                                fieldWithPath("description").description("상품 설명"),
                                fieldWithPath("stock").description("상품 재고"),
                                fieldWithPath("price").description("판매 가격"),
                                fieldWithPath("salable").description("판매 가능 여부"),
                                fieldWithPath("categories[]").description("카테고리")
                        )
                    )
                );
    }

    @Test
    public void modify_stock() throws Exception {
        //given

        ProductStockModifyRequestDto productStockDto1 = new ProductStockModifyRequestDto(1, 5);
        ProductStockModifyRequestDto productStockDto2 = new ProductStockModifyRequestDto(2, -2);

        String email = "test@example.com";
        MemberDto memberDto = getMemberDto(email);


        Mockito.when(memberService.getMember(email))
                .thenReturn(memberDto);

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/stock")
                    .content(objectMapper.writeValueAsString(Arrays.asList(productStockDto1, productStockDto2)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("modify-product-stock",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        requestFields(
                                fieldWithPath("[].productId").description("상품 고유 번호"),
                                fieldWithPath("[].stock").description("수정될 재고 수량(음수일 경우 재고에서 삭감, 양수일 경우 재고에서 추가)")
                        )
                    )
                );
    }

    @Test
    public void modify() throws Exception {
        //given
        String email = "test@example.com";
        MemberDto memberDto = getMemberDto(email);

        Mockito.when(memberService.getMember(email))
                .thenReturn(memberDto);

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

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

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/{productId}", 1L)
                    .content(objectMapper.writeValueAsString(productModifyRequestDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("modify-product",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        pathParameters(
                                parameterWithName("productId").description("상품 번호")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정될 상품명"),
                                fieldWithPath("code").description("수정될 상품 고유 코드"),
                                fieldWithPath("brandName").description("수정될 브랜드 명"),
                                fieldWithPath("description").description("수정될 상품 설명"),
                                fieldWithPath("stock").description("수정될 상품 재고"),
                                fieldWithPath("price").description("수정될 판매 가격"),
                                fieldWithPath("salable").description("수정될 판매 가능 여부"),
                                fieldWithPath("categories[]").description("수정될 카테고리")
                        )
                    )
                );
    }


    @Test
    public void get_product() throws Exception {
        //given
        String email = "test@example.com";

        Product product = Product.Builder.builder()
                .brandName("리바이스")
                .code("AA123")
                .description("빈티지")
                .name("청바지")
                .salable(true)
                .price(5000L)
                .stock(20)
                .build()
                .toEntity();

        ProductCategory productCategory = ProductCategory.createProductCategory(product, Category.createCategory("의류"));
        product.classifyCategories(Arrays.asList(productCategory));

        Mockito.when(productService.findOne(1L))
                .thenReturn(product);

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/{productId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-product",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        pathParameters(
                                parameterWithName("productId").description("상품 번호")
                        ),
                        responseFields(
                                fieldWithPath("result").description("API 요청 결과 SUCCESS / FAIL"),
                                fieldWithPath("messages").description("API 요청 메시지"),
                                fieldWithPath("error").description("API 요청 에러"),
                                fieldWithPath("fieldErrors").description("API form validation 에러"),
                                fieldWithPath("data").description("API 요청 데이터"),
                                fieldWithPath("data.name").description("상품명"),
                                fieldWithPath("data.code").description("상품 고유 코드"),
                                fieldWithPath("data.brandName").description("브랜드 명"),
                                fieldWithPath("data.description").description("상품 설명"),
                                fieldWithPath("data.stock").description("상품 재고"),
                                fieldWithPath("data.price").description("판매 가격"),
                                fieldWithPath("data.salable").description("판매 가능 여부"),
                                fieldWithPath("data.categories[]").description("카테고리")
                        )
                    )
                );
    }

    @Test
    public void get_product_by_search_query() throws Exception {
        //given
        String email = "test@example.com";

        Product product = Product.Builder.builder()
                .brandName("캘빈클라인")
                .code("AA123")
                .description("빈티지")
                .name("청바지")
                .salable(true)
                .price(5000L)
                .stock(20)
                .build()
                .toEntity();

        ProductCategory productCategory = ProductCategory.createProductCategory(product, Category.createCategory("빈티지"));
        product.classifyCategories(Arrays.asList(productCategory));

        Mockito.when(productService.findOne(1L))
                .thenReturn(product);

        PageRequest productPageRequest = ProductPageRequestDto.createProductPageRequest(0, 5);
        ProductSpecificationQueryDto productSpecificationQueryDto = new ProductSpecificationQueryDto("바지",
                "AA123",
                "캘빈클라인",
                5000L,
                100000L,
                Arrays.asList("빈티지", "슬림핏"));

        Map<ProductSearchKey, Object> queryMap = productSpecificationQueryDto.createQueryMap();

        Mockito.when(productSearchService.findAllByQuery(productPageRequest, queryMap))
                .thenReturn(Arrays.asList(product));

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        Map<String, Object> map =
                objectMapper.convertValue(productSpecificationQueryDto, new TypeReference<Map<String, Object>>() {});

//        LinkedMultiValueMap<String, Object> linkedMultiValueMap = new LinkedMultiValueMap<>();
//        map.entrySet().forEach(e -> linkedMultiValueMap.add(e.getKey(), e.getValue()));

//        String s = toQueryString(map);

        String s = "name=바지&code=AA123&brandName=캘빈클라인&minPrice=5000&maxPrice=100000&categories=빈티지,슬림핏";

        //when
        //then
        String urlTemplate = "/search?" + s;
        System.out.println(urlTemplate);
        mockMvc.perform(RestDocumentationRequestBuilders.get(urlTemplate)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-products",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        requestParameters(
                                parameterWithName("name").description("상품 명"),
                                parameterWithName("code").description("상품 코드"),
                                parameterWithName("brandName").description("브랜드 명"),
                                parameterWithName("minPrice").description("최소 가격"),
                                parameterWithName("maxPrice").description("최대 가격"),
                                parameterWithName("categories").description("카테고리 종류")
                        ),
                        responseFields(
                                fieldWithPath("result").description("API 요청 결과 SUCCESS / FAIL"),
                                fieldWithPath("messages").description("API 요청 메시지"),
                                fieldWithPath("error").description("API 요청 에러"),
                                fieldWithPath("fieldErrors").description("API form validation 에러"),
                                fieldWithPath("data[]").description("API 요청 데이터"),
                                fieldWithPath("data[].name").description("상품명"),
                                fieldWithPath("data[].code").description("상품 고유 코드"),
                                fieldWithPath("data[].brandName").description("브랜드 명"),
                                fieldWithPath("data[].description").description("상품 설명"),
                                fieldWithPath("data[].stock").description("상품 재고"),
                                fieldWithPath("data[].price").description("판매 가격"),
                                fieldWithPath("data[].salable").description("판매 가능 여부"),
                                fieldWithPath("data[].categories[]").description("카테고리")
                        )
                    )
                );
    }

    private String toQueryString(Map<String, Object> map) {
        StringBuilder qs = new StringBuilder();
        for (String key : map.keySet()){

            if (key.equals("categories")) {
                List<String> categories = (List<String>) map.get(key);
                qs.append("categories=");
                for (int i = 0; i < categories.size(); i++) {
                    qs.append(categories.get(i) + ",");
                }

                qs.deleteCharAt(qs.length() - 1);

            }else {
                qs.append(key);
                qs.append("=");
                qs.append(map.get(key));
                qs.append("&");
            }

        }

        // delete last '&'
        if (qs.length() != 0) {
            qs.deleteCharAt(qs.length() - 1);
        }
        return qs.toString();
    }

    private MemberDto getMemberDto(String email) {
        return MemberDto.builder()
                .address("서울 강남")
                .name("choi")
                .password("qwer!23")
                .phone("010-3132-1089")
                .zipCode(12345)
                .enable(true)
                .authorities(Arrays.asList("MEMBER"))
                .email(email)
                .memberId(1L)
                .build();
    }
}
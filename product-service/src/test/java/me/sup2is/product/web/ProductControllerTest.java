package me.sup2is.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.product.config.RestDocsConfiguration;
import me.sup2is.product.domain.Category;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.ProductCategory;
import me.sup2is.product.domain.dto.MemberDto;
import me.sup2is.product.web.dto.ProductModifyRequestDto;
import me.sup2is.product.web.dto.ProductRequestDto;
import me.sup2is.product.domain.dto.ProductStockDto;
import me.sup2is.product.service.MemberService;
import me.sup2is.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
@Import({JwtTokenUtil.class, RestDocsConfiguration.class})
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

        ProductStockDto productStockDto1 = new ProductStockDto(1, 5);
        ProductStockDto productStockDto2 = new ProductStockDto(2, -2);

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

        Product.Builder builder = new Product.Builder();
        builder.setBrandName("리바이스")
                .setCode("AA123")
                .setDescription("빈티지")
                .setName("청바지")
                .setSalable(true)
                .setPrice(5000L)
                .setStock(20);
        Product product = Product.createProduct(builder);

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
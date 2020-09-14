package me.sup2is.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.product.config.RestDocsConfiguration;
import me.sup2is.product.domain.dto.MemberDto;
import me.sup2is.product.domain.dto.ProductRequestDto;
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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

        //when
        //then
        mockMvc.perform(put("/stock")
                .content(objectMapper.writeValueAsString(Arrays.asList(productStockDto1, productStockDto2)))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
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
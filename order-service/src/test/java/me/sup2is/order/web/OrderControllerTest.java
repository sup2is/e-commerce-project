package me.sup2is.order.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.order.config.RestDocsConfiguration;
import me.sup2is.order.domain.dto.MemberDto;
import me.sup2is.order.domain.dto.OrderItemRequestDto;
import me.sup2is.order.domain.dto.OrderRequestDto;
import me.sup2is.order.service.MemberService;
import me.sup2is.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
@Import({RestDocsConfiguration.class, JwtTokenUtil.class})
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrderService orderService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Test
    @DisplayName("주문 테스트")
    public void order() throws Exception {
        //given
        OrderItemRequestDto orderItemRequestDto1 = new OrderItemRequestDto(1L, 20000L, 4, 0);
        OrderItemRequestDto orderItemRequestDto2 = new OrderItemRequestDto(1L, 30000L, 2, 0);

        List<OrderItemRequestDto> orderItemRequestDto = Arrays.asList(orderItemRequestDto1, orderItemRequestDto2);

        OrderRequestDto orderRequestDto = new OrderRequestDto("주문 받는 주소", orderItemRequestDto);

        String email = "test@example.com";
        MemberDto memberDto = MemberDto.builder()
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

        Mockito.when(memberService.getMember(email))
                .thenReturn(memberDto);

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                    .content(objectMapper.writeValueAsString(orderRequestDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(200))
                .andDo(document("create-order",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        requestFields(
                                fieldWithPath("address").type(JsonFieldType.STRING).description("상품 수령 주소"),
                                fieldWithPath("orderItems").type(JsonFieldType.ARRAY).description("주문 아이템 리스트"),
                                fieldWithPath("orderItems[].productId").type(JsonFieldType.NUMBER).description("상품 번호"),
                                fieldWithPath("orderItems[].price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("orderItems[].count").type(JsonFieldType.NUMBER).description("주문 수량"),
                                fieldWithPath("orderItems[].discountRate").type(JsonFieldType.NUMBER).description("할인율")
                        )
                ));

    }

    @Test
    @DisplayName("/order 요청시 orderItemRequest 파라미터, 주문 수령 주소 검사")
    public void order_invalid_parameter() throws Exception {
        //given
        OrderItemRequestDto orderItemRequestDto1 = new OrderItemRequestDto(1L, 20000L, 4, 0);
        OrderItemRequestDto orderItemRequestDto2 = new OrderItemRequestDto(1L, null, 2, 0);

        List<OrderItemRequestDto> orderItemRequestDto = Arrays.asList(orderItemRequestDto1, orderItemRequestDto2);

        OrderRequestDto orderRequestDto = new OrderRequestDto(null, orderItemRequestDto);

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .content(objectMapper.writeValueAsString(orderRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));

    }

    @Test
    @DisplayName("/order 요청시 orderItemRequest 사이즈 검사")
    public void order_empty_order_item() throws Exception {
        //given
        OrderRequestDto orderRequestDto = new OrderRequestDto("test@example.com", new ArrayList<>());

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .content(objectMapper.writeValueAsString(orderRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));

        //then

    }

    @Test
    @DisplayName("주문 취소")
    public void order_cancel() throws Exception {
        //given
        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/1/cancel"))
                .andDo(print())
                .andExpect(status().is(200));
        //then

    }
}
package me.sup2is.order.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.order.config.RestDocsConfiguration;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.dto.*;
import me.sup2is.order.exception.OrderNotFoundException;
import me.sup2is.order.service.MemberService;
import me.sup2is.order.service.OrderService;
import me.sup2is.order.web.dto.*;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        MemberDto memberDto = getMemberDto(email);

        Mockito.when(memberService.getMember(email))
                .thenReturn(memberDto);

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        //when
        //then
        mockMvc.perform(post("/")
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
        mockMvc.perform(post("/")
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
        mockMvc.perform(post("/")
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
        String email = "test@example.com";
        MemberDto memberDto = getMemberDto(email);

        Mockito.when(memberService.getMember(email))
                .thenReturn(memberDto);

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/{orderId}/cancel", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().is(200))
                    .andDo(document("cancel-order",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                            ),
                            pathParameters(parameterWithName("orderId").description("주문 번호")))
                    );


    }

    @Test
    @DisplayName("주문 수정")
    public void modify_order() throws Exception {
        //given
        String email = "test@example.com";
        MemberDto memberDto = getMemberDto(email);

        Mockito.when(memberService.getMember(email))
                .thenReturn(memberDto);

        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        ModifyOrderRequestDto modifyOrderRequestDto =
                new ModifyOrderRequestDto("변경될 주소",
                Arrays.asList(new ModifyOrderItemRequestDto(1L, 5, 0)));


        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/{orderId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyOrderRequestDto)))
                    .andDo(print())
                    .andExpect(status().is(200))
                    .andDo(document("modify-order",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                            ),
                            pathParameters(parameterWithName("orderId").description("주문 번호")),
                            requestFields(
                                    fieldWithPath("address").description("변경 될 주소"),
                                    fieldWithPath("modifyOrderItems").description("변경할 아이템 리스트"),
                                    fieldWithPath("modifyOrderItems[].productId").description("변경할 상품의 고유번호"),
                                    fieldWithPath("modifyOrderItems[].count").description("변경 될 상품의 개수"),
                                    fieldWithPath("modifyOrderItems[].discountRate").description("변경 될 상품의 할인율 (미구현)"))
                            )
                    );


    }

    @Test
    @DisplayName("주문 조회 (1개)")
    public void get_order() throws Exception, OrderNotFoundException {
        //given
        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        OrderItem orderItem1 = getOrderItem(1L, 10000L, 2);
        OrderItem orderItem2 = getOrderItem(22L, 50000L, 1);

        orderItem1.setPrice(10000L);
        orderItem2.setPrice(50000L);

        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);

        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        order.setTotalPrice();

        FieldUtils.writeField(order, "memberId", 1L, true);

        Mockito.when(orderService.findOne(1L))
                .thenReturn(order);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/{orderId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().is(200))
                    .andDo(document("get-order",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("orderId").description("주문 번호")
                            ),
                            responseFields(
                                    fieldWithPath("result").description("API 요청 결과 SUCCESS / FAIL"),
                                    fieldWithPath("messages").description("API 요청 메시지"),
                                    fieldWithPath("error").description("API 요청 에러"),
                                    fieldWithPath("fieldErrors").description("API form validation 에러"),
                                    fieldWithPath("data").description("API 요청 데이터"),
                                    fieldWithPath("data.memberId").description("주문 회원 번호"),
                                    fieldWithPath("data.address").description("주문 주소"),
                                    fieldWithPath("data.totalPrice").description("주문 총 금액"),
                                    fieldWithPath("data.orderStatus").description("주문 상태"),
                                    fieldWithPath("data.orderItems").description("상품"),
                                    fieldWithPath("data.orderItems[].productId").description("상품 번호"),
                                    fieldWithPath("data.orderItems[].price").description("상품 금액"),
                                    fieldWithPath("data.orderItems[].count").description("상품 주문 개수"),
                                    fieldWithPath("data.orderItems[].discountRate").description("할인율"),
                                    fieldWithPath("data.orderItems[].orderStatus").description("주문 개별 상태"))
                            )
                    );
    }

    @Test
    @DisplayName("주문 조회 (n개)")
    public void get_orders() throws Exception, OrderNotFoundException {
        //given
        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        OrderItem orderItem1 = getOrderItem(1L, 10000L, 2);
        OrderItem orderItem2 = getOrderItem(22L, 50000L, 1);

        orderItem1.setPrice(10000L);
        orderItem2.setPrice(50000L);

        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);

        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        Order order2 = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        FieldUtils.writeField(order, "memberId", 1L, true);
        FieldUtils.writeField(order2, "memberId", 1L, true);
        FieldUtils.writeField(order2, "memberId", 1L, true);

        order.setTotalPrice();
        order2.setTotalPrice();

        PageRequest orderPageRequest = OrderPageRequestDto.createOrderPageRequest(0, 5);

        Mockito.when(orderService.findAll(orderPageRequest, 1L))
                .thenReturn(Arrays.asList(order, order2));

        MemberDto memberDto = getMemberDto(email);

        Mockito.when(memberService.getMember(email))
                .thenReturn(memberDto);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("pageNo", "0")
                        .param("pageSize", "5"))
                    .andDo(print())
                    .andExpect(status().is(200))
                    .andDo(document("get-orders",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                            ),
                            requestParameters(
                                    parameterWithName("pageNo").description("페이지 번호"),
                                    parameterWithName("pageSize").description("페이지 사이즈")
                            ),
                            responseFields(
                                    fieldWithPath("result").description("API 요청 결과 SUCCESS / FAIL"),
                                    fieldWithPath("messages").description("API 요청 메시지"),
                                    fieldWithPath("error").description("API 요청 에러"),
                                    fieldWithPath("fieldErrors").description("API form validation 에러"),
                                    fieldWithPath("data[]").description("API 요청 데이터"),
                                    fieldWithPath("data[].memberId").description("주문 회원 번호"),
                                    fieldWithPath("data[].address").description("주문 주소"),
                                    fieldWithPath("data[].totalPrice").description("주문 총 금액"),
                                    fieldWithPath("data[].orderStatus").description("주문 상태"),
                                    fieldWithPath("data[].orderItems").description("상품"),
                                    fieldWithPath("data[].orderItems[].productId").description("상품 번호"),
                                    fieldWithPath("data[].orderItems[].price").description("상품 금액"),
                                    fieldWithPath("data[].orderItems[].count").description("상품 주문 개수"),
                                    fieldWithPath("data[].orderItems[].discountRate").description("할인율"),
                                    fieldWithPath("data[].orderItems[].orderStatus").description("주문 개별 상태"))
                            )
                    );
    }

    private OrderItem getOrderItem(long productId, long price, int count) {

        return OrderItem.Builder.builder()
                .productId(productId)
                .discountRate(0)
                .count(count)
                .build()
                .toEntity();

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


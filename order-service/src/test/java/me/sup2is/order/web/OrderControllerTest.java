package me.sup2is.order.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.dto.OrderItemRequestDto;
import me.sup2is.order.domain.dto.OrderRequestDto;
import me.sup2is.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrderService orderService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("/order 요청시 orderItemRequest 파라미터 검사")
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
                .andExpect(status().is(400))
                .andReturn();


    }

    @Test
    @DisplayName("/order 요청시 orderItemRequest 사이즈 검사")
    public void order_empty_order_item() throws Exception {
        //given
        OrderRequestDto orderRequestDto = new OrderRequestDto(1L, new ArrayList<>());

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
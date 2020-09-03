package me.sup2is.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.ProductCategory;
import me.sup2is.product.domain.dto.ProductRequestDto;
import me.sup2is.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @Autowired
    ObjectMapper objectMapper;

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
                .sellerId(1L)
                .stock(1000)
                .build();

        doAnswer(i -> null)
                .when(productService)
                .register(productRequestDto.toEntity(), productRequestDto.getCategories());

        //when
        //then
        mockMvc.perform(post("/")
                .content(objectMapper.writeValueAsString(productRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }




}
package me.sup2is.product.service;

import com.netflix.discovery.converters.Auto;
import me.sup2is.product.domain.dto.ProductRequestDto;
import me.sup2is.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ProductService.class)
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @MockBean
    ProductRepository productRepository;

    @MockBean
    CategoryService categoryService;

    @Test
    public void register_with_categories() {
        //given
        List<String> categoryNames = Arrays.asList("의류", "바지", "청바지");

        ProductRequestDto productRequestDto = new ProductRequestDto(1L,
                "청바지",
                "AA123",
                "리바이스",
                "빈티지",
                5,
                10000L,
                true,
                categoryNames);




        //when

        //then

    }


}
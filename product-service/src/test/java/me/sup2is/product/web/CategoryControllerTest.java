package me.sup2is.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.product.web.dto.CategoryRequestDto;
import me.sup2is.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CategoryService categoryService;

    @Test
    public void add_category() throws Exception {
        //given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("바지");

        doAnswer(i -> null)
                .when(categoryService).add(categoryRequestDto.toEntity());

        //when
        //then
        mockMvc.perform(post("/category")
                .content(objectMapper.writeValueAsString(categoryRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

}
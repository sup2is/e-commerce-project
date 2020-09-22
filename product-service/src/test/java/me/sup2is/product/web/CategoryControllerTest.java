package me.sup2is.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.product.config.MockTestConfiguration;
import me.sup2is.product.config.RestDocsConfiguration;
import me.sup2is.product.domain.dto.MemberDto;
import me.sup2is.product.web.dto.CategoryRequestDto;
import me.sup2is.product.service.CategoryService;
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

import static org.mockito.Mockito.doAnswer;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
@Import({RestDocsConfiguration.class, MockTestConfiguration.class, JwtTokenUtil.class})
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CategoryService categoryService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Test
    public void add_category() throws Exception {
        //given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("바지");

        doAnswer(i -> null)
                .when(categoryService).add(categoryRequestDto.toEntity());

        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);


        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/category")
                .content(objectMapper.writeValueAsString(categoryRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("add-category", requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("카테고리명")
                        )
                    )
                );
    }

}
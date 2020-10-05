package me.sup2is.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.client.dto.MemberDto;
import me.sup2is.config.MockTestConfiguration;
import me.sup2is.config.RestDocsConfiguration;
import me.sup2is.dto.AuthenticationResponseDto;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.service.AuthenticationService;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import({RestDocsConfiguration.class, MockTestConfiguration.class})
@AutoConfigureRestDocs
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void generate_jwt_token() throws Exception {

        //given
        Map<String, String> userMap = new HashMap<>();
        String email = "choi@example.com";
        String password = "qwer!23";

        userMap.put("username", email);
        userMap.put("password", password);

        MemberDto expect = MemberDto.builder()
                .address("서울 강남")
                .authorities(Arrays.asList("MEMBER"))
                .email(email)
                .enable(true)
                .name("choi")
                .password(password)
                .phone("010-3132-1089")
                .zipCode(12345)
                .build();

        Mockito.when(authenticationService.authenticateByEmailAndPassword(email, password))
                .thenReturn(expect);
        String expectAccessToken = "test_access_token";
        Mockito.when(jwtTokenUtil.generateToken(email, JwtTokenType.AUTH))
                .thenReturn(expectAccessToken);
        String expectRefreshToken = "test_refresh_token";
        Mockito.when(jwtTokenUtil.generateToken(email, JwtTokenType.REFRESH))
                .thenReturn(expectRefreshToken);

        AuthenticationResponseDto authenticationFromTokens =
                AuthenticationResponseDto
                        .createAuthenticationFromTokens(expectAccessToken, expectRefreshToken);

        JsonResult<?> jsonResult = new JsonResult<>(authenticationFromTokens);

        //when
        //then
        mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(userMap))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(jsonResult)))
                .andDo(document("get-auth",
                        requestFields(
                                fieldWithPath("username").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 패스워드")
                        ),
                        responseFields(
                                fieldWithPath("result").description("API 요청 결과 SUCCESS / FAIL"),
                                fieldWithPath("messages").description("API 요청 메시지"),
                                fieldWithPath("error").description("API 요청 에러"),
                                fieldWithPath("fieldErrors").description("API form validation 에러"),
                                fieldWithPath("data").description("API 요청 데이터"),
                                fieldWithPath("data.accessToken").description("access token"),
                                fieldWithPath("data.refreshToken").description("refresh token")
                        )
                    )
                );
    }


    @Test
    public void generate_jwt_token_password_not_equals() throws Exception {

        //given
        Map<String, String> userMap = new HashMap<>();
        String email = "choi@example.com";
        String password = "qwer!23";

        userMap.put("username", email);
        userMap.put("password", password);

        Mockito.when(authenticationService.authenticateByEmailAndPassword(email, password))
                .thenThrow(new BadCredentialsException("Password not matched"));

        JsonResult<?> expect = new JsonResult<>(new BadCredentialsException("Password not matched"));


        //when
        //then
        mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(userMap))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(objectMapper.writeValueAsString(expect)));

    }
}
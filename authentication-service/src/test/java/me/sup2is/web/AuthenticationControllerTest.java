package me.sup2is.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.client.dto.MemberClientDto;
import me.sup2is.dto.AuthenticationResponseDto;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
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

        MemberClientDto expect = MemberClientDto.builder()
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
                .andExpect(content().string(objectMapper.writeValueAsString(jsonResult)));
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
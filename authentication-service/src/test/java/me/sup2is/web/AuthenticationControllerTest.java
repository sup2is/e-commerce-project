package me.sup2is.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(classes = RedisTestConfig.class)
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtAuthenticationGenerator jwtAuthenticationGenerator;

    @MockBean(name = "memberServiceClient")
    MemberServiceClient memberServiceClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void generate_jwt_token() throws Exception {

        //given
        String email = "choi@example.com";
        String password = "qwer!23";

        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("MEMBER"));
        User user = new User(email, password, grantedAuthorities);

        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil("temp");
        JwtAuthenticationGenerator real = new JwtAuthenticationGenerator(jwtTokenUtil);

        AuthenticationResponseDto dto =
                real.createJwtAuthenticationFromUserDetails(user);

        JsonResult<AuthenticationResponseDto> result = new JsonResult<>(dto);

        User encryptedUser = new User(email, passwordEncoder.encode(password), grantedAuthorities);

        JsonResult<User> memberResult = new JsonResult<>(encryptedUser);

        Mockito.when(memberServiceClient.getMember(email)).thenReturn(memberResult);
        Mockito.when(jwtAuthenticationGenerator.createJwtAuthenticationFromUserDetails(user))
                .thenReturn(dto);

        //when
        //then
        mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(result)));
    }


    @Test
    public void generate_jwt_token_password_not_equals() throws Exception {

        //given
        String email = "choi@example.com";
        String password = "qwer!23";

        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("MEMBER"));
        User user = new User(email, password, grantedAuthorities);

        JsonResult<User> memberResult = new JsonResult<>(user);

        Mockito.when(memberServiceClient.getMember(email)).thenReturn(memberResult);

        //when
        //then
        mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(403));
    }
}
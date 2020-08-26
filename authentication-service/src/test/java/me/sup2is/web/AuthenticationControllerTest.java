package me.sup2is.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        RedisConnectionFactory.class
        , BCryptPasswordEncoder.class
        , AuthenticationController.class})
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class
        , SecurityAutoConfiguration.class})
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtAuthenticationGenerator jwtAuthenticationGenerator;

    @MockBean(name = "memberServiceClient")
    MemberServiceClient memberServiceClient;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Test
    public void generate_jwt_token() throws Exception {

        //given
        Map<String, String> userMap = new HashMap<>();
        String email = "choi@example.com";
        String password = "qwer!23";
        userMap.put("username", email);
        userMap.put("password", password);

        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("MEMBER"));
        User user = new User(email, password, grantedAuthorities);

        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil("temp");
        JwtAuthenticationGenerator real = new JwtAuthenticationGenerator(jwtTokenUtil);

        AuthenticationResponseDto dto =
                real.createJwtAuthenticationFromUserDetails(user);

        JsonResult<AuthenticationResponseDto> result = new JsonResult<>(dto);

        Member encryptedUser = new Member(email, password, grantedAuthorities);

        JsonResult<Member> memberResult = new JsonResult<>(encryptedUser);

        Mockito.when(memberServiceClient.getMember(email)).thenReturn(memberResult);
        Mockito.when(jwtAuthenticationGenerator.createJwtAuthenticationFromUserDetails(user))
                .thenReturn(dto);
        Mockito.when(passwordEncoder.matches(password,password)).thenReturn(true);

        //when
        //then
        mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(userMap))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(result)));
    }


    @Test
    public void generate_jwt_token_password_not_equals() throws Exception {

        //given
        Map<String, String> userMap = new HashMap<>();
        String email = "choi@example.com";
        String password = "qwer!23";
        userMap.put("username", email);
        userMap.put("password", password);


        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("MEMBER"));
        Member user = new Member(email, password, grantedAuthorities);

        JsonResult<Member> memberResult = new JsonResult<>(user);

        Mockito.when(memberServiceClient.getMember(email)).thenReturn(memberResult);
        Mockito.when(passwordEncoder.matches(password,password)).thenReturn(false);

        //when
        //then
        assertThatThrownBy(() ->
                mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(userMap))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())).hasCause(new BadCredentialsException("Password not matched"));

    }
}
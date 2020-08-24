package me.sup2is.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.MemberService;
import me.sup2is.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
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

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void generateJwtToken() throws Exception {

        //given
        Map<String, String> map = new HashMap<>();
        String username = "choi@example.com";
        String password = "qwer!23";
        map.put("username", username);
        map.put("password", password);

        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("MEMBER"));
        User user = new User(username, password, grantedAuthorities);

        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil("temp");
        JwtAuthenticationGenerator real = new JwtAuthenticationGenerator(jwtTokenUtil);

        AuthenticationResponseDto dto =
                real.createJwtAuthenticationFromUserDetails(user);

        JsonResult<AuthenticationResponseDto> result = new JsonResult<>(dto);

        Mockito.when(memberService.loadUserByUsername(username)).thenReturn(user);
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
}
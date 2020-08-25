package me.sup2is;

import me.sup2is.client.MemberServiceClient;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.web.JsonResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RedisTestConfig.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @MockBean(name = "memberServiceClient")
    MemberServiceClient memberServiceClient;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("정상적인 token을 갖고 있는 사람이 access했을 경우")
    public void access_auth_user_valid_token() throws Exception{
        //given


        String email = "choi@example.com";
        String password = "qwer!23";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_MEMBER");
        User user = new User(email, passwordEncoder.encode(password), grantedAuthorities);


        JsonResult<User> result = new JsonResult<>(user);

        String accessToken = jwtTokenUtil.generateToken("choi@example.com", JwtTokenType.AUTH);
        Mockito.when(memberServiceClient.getMember(email)).thenReturn(result);

        //when & then
        mockMvc.perform(get("/api/")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andDo(print())
            .andExpect(status().is(404));
    }

    @Test
    @DisplayName("토큰이 없는 사람이 access했을 경우")
    public void access_invalid_user_empty_access_token() throws Exception{
        //given

        //when & then

        mockMvc.perform(get("/api/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer "))
            .andDo(print())
            .andExpect(status().is(401));

    }



}
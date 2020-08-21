package me.sup2is;

import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @MockBean(name = "authServiceClient")
    AuthServiceClient authServiceClient;

    @Test
    public void access_auth_user() throws Exception{
        //given
        String accessToken = jwtTokenUtil.generateToken("test@example.com", JwtTokenType.AUTH);

        String username = "choi@example.com";
        String password = "qwer!23";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_MEMBER");
        Member member = new Member(username, password, grantedAuthorities);

        Mockito.when(authServiceClient.getMember(accessToken)).thenReturn(member);

        //when & then
        mockMvc.perform(get("/api/")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andDo(print())
            .andExpect(status().is(404));
    }

}
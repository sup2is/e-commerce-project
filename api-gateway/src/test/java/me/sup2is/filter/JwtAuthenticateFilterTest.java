package me.sup2is.filter;

import me.sup2is.client.MemberServiceClient;
import me.sup2is.client.dto.MemberDto;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.service.MemberService;
import me.sup2is.web.JsonResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({JwtTokenUtil.class})
class JwtAuthenticateFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @MockBean
    MemberService memberService;

    @Test
    @DisplayName("정상적인 token을 갖고 있는 사람이 access했을 경우")
    public void access_auth_user_valid_token() throws Exception{
        //given
        String email = "choi@example.com";

        MemberDto expect = MemberDto.builder()
                .address("서울 강남")
                .authorities(Arrays.asList("MEMBER"))
                .email(email)
                .enable(true)
                .name("choi")
                .password("qwer!23")
                .phone("010-3132-1089")
                .zipCode(12345)
                .build();

        String accessToken = jwtTokenUtil.generateToken("choi@example.com", JwtTokenType.AUTH);
        Mockito.when(memberService.getMember(email)).thenReturn(expect);

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
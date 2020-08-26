package me.sup2is.member.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.member.service.MemberService;
import me.sup2is.web.JsonResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void get_member() throws Exception {
        //given
        String email = "choi@example.com";
        User user = new User(email, "password", Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")));

        JsonResult<User> result = new JsonResult<>(user);

        Mockito.when(memberService.loadUserByUsername(email)).thenReturn(user);

        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post("/member")
                                            .content(email))
                    .andExpect(content().string(objectMapper.writeValueAsString(result)));

    }


}
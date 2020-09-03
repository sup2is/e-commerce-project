package me.sup2is.member.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.member.client.dto.MemberClientDto;
import me.sup2is.member.domain.Member;
import me.sup2is.member.service.MemberService;
import me.sup2is.web.JsonResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CredentialControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void get_credential() throws Exception {
        //given
        String email = "choi@example.com";

        Member.Builder builder = new Member.Builder();
        Member member = Member.createMember(builder.email(email)
                .address("서울 강남")
                .name("choi")
                .password("qwer!23")
                .phone("010-3132-1089")
                .zipCode(12345)
                .enable(true)
                .authorities(Arrays.asList("MEMBER")));

        MemberClientDto memberClientDto = MemberClientDto.createMemberClientDto(member);

        JsonResult<MemberClientDto> result = new JsonResult<>(memberClientDto);

        Mockito.when(memberService.findByEmail(email)).thenReturn(member);

        //when
        //then
        String contentAsString = mockMvc.perform(
                MockMvcRequestBuilders.post("/credential")
                        .content(email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(result), contentAsString);

    }


}
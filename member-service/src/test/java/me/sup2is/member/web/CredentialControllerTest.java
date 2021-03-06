package me.sup2is.member.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.member.client.dto.MemberDto;
import me.sup2is.member.config.MockTestConfiguration;
import me.sup2is.member.domain.Member;
import me.sup2is.member.service.MemberService;
import me.sup2is.web.JsonResult;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import({JwtTokenUtil.class, MockTestConfiguration.class})
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

        Member member = Member.Builder.builder()
                .address("서울시 강남구")
                .email(email)
                .name("sup2is")
                .password("qwer!23")
                .phone("010-3132-1089")
                .zipCode(65482)
                .authorities(Arrays.asList("MEMBER"))
                .build()
                .toEntity();

        FieldUtils.writeField(member, "id", 1L, true);

        MemberDto memberDto = MemberDto.createMemberDto(member);
        Mockito.when(memberService.findMemberDto(email)).thenReturn(memberDto);

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

        Assertions.assertEquals(objectMapper.writeValueAsString(memberDto), contentAsString);

    }


}
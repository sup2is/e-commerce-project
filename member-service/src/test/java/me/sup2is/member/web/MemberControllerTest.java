package me.sup2is.member.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.member.domain.Member;
import me.sup2is.member.domain.dto.MemberRequestDto;
import me.sup2is.member.service.MemberService;
import me.sup2is.web.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @Test
    public void create_member() throws Exception {
        //given
        MemberRequestDto memberRequestDto =
                new MemberRequestDto("test01@example.com"
                    ,"qwer!23"
                    , "test"
                    , "test"
                    , 123
                    , "010-1234-1234");

        Member member = memberRequestDto.toEntity();

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                    .content(objectMapper.writeValueAsString(member))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void create_member_with_validate() throws Exception {
        //given
        MemberRequestDto memberRequestDto =
                new MemberRequestDto("my-validate"
                    ,"1"
                    , "test"
                    , "test"
                    , 123
                    , "12341234");

        Member member = memberRequestDto.toEntity();

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                    .content(objectMapper.writeValueAsString(member))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }


}
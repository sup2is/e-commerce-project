package me.sup2is.member.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.member.config.RestDocsConfiguration;
import me.sup2is.member.domain.Member;
import me.sup2is.member.domain.dto.MemberRequestDto;
import me.sup2is.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
@Import({RestDocsConfiguration.class, JwtTokenUtil.class})
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

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                    .content(objectMapper.writeValueAsString(memberRequestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("save-member",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 (중복 X)"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호 (최소 6자리)"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름 or 닉네임"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("회원주소"),
                                fieldWithPath("zipCode").type(JsonFieldType.NUMBER).description("우편번호"),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호")
                        )
                ));
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

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                    .content(objectMapper.writeValueAsString(memberRequestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

}
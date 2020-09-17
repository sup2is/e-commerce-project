package me.sup2is.member.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.member.config.RestDocsConfiguration;
import me.sup2is.member.domain.Member;
import me.sup2is.member.domain.dto.MemberModifyRequestDto;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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

    @Autowired
    JwtTokenUtil jwtTokenUtil;

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


        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                    .content(objectMapper.writeValueAsString(memberRequestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    public void modify_member() throws Exception {
        //given
        MemberModifyRequestDto memberModifyRequestDto =
                new MemberModifyRequestDto("new-password"
                    , "test"
                    , "test"
                    , 123
                    , "010-3132-1000");

        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/")
                    .content(objectMapper.writeValueAsString(memberModifyRequestDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(org.apache.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(200))
                .andDo(document("modify-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("변경될 회원 비밀번호 (최소 6자리)"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경될 회원 이름 or 닉네임"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("변경될 회원주소"),
                                fieldWithPath("zipCode").type(JsonFieldType.NUMBER).description("변경될 우편번호"),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("변경될 휴대폰 번호")
                        )
                ));
    }

    @Test
    public void get_member() throws Exception {
        //given

        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email, JwtTokenType.AUTH);


        Member.Builder builder = new Member.Builder();
        Member member = getMember(builder, email,"서울시 강남구", "sup2is", "qwer!23", "010-3132-1089", 12345);

        when(memberService.findByEmail(email))
                .thenReturn(member);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/")
                .header(org.apache.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(200))
                .andDo(document("get-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        responseFields(
                                fieldWithPath("result").description("API 요청 결과 SUCCESS / FAIL"),
                                fieldWithPath("messages").description("API 요청 메시지"),
                                fieldWithPath("error").description("API 요청 에러"),
                                fieldWithPath("fieldErrors").description("API form validation 에러"),
                                fieldWithPath("data").description("API 요청 데이터"),
                                fieldWithPath("data.email").description("회원 이메일"),
                                fieldWithPath("data.address").description("회원 주소"),
                                fieldWithPath("data.zipCode").description("우편번호"),
                                fieldWithPath("data.name").description("회원명"),
                                fieldWithPath("data.phone").description("휴대폰 번호")
                        )
                ));
    }


    private Member getMember(Member.Builder builder, String email, String address, String sup2is, String password, String phone, int zipCode) {
        return Member.createMember(builder.address(address)
                .email(email)
                .name(sup2is)
                .password(password)
                .phone(phone)
                .zipCode(zipCode)
                .authorities(Arrays.asList("MEMBER")));
    }
}
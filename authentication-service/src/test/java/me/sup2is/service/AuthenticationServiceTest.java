package me.sup2is.service;

import me.sup2is.client.MemberServiceClient;
import me.sup2is.client.dto.MemberDto;
import me.sup2is.web.JsonResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = AuthenticationService.class)
class AuthenticationServiceTest {

    @Autowired
    AuthenticationService authenticationService;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    MemberServiceClient memberServiceClient;

    @Test
    public void authenticate_by_email_and_password() {
        //given

        //when
        String password = "qwer!23";
        String email = "test@exmaple.com";

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

        Mockito.when(memberServiceClient.getMember(email))
                .thenReturn(expect);
        Mockito.when(passwordEncoder.matches(password, expect.getPassword()))
                .thenReturn(true);

        //then
        MemberDto memberDto = authenticationService.authenticateByEmailAndPassword(email, password);
        assertEquals(expect.toString(), memberDto.toString());

    }

}
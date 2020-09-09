package me.sup2is.service;

import com.netflix.discovery.converters.Auto;
import me.sup2is.client.MemberServiceClient;
import me.sup2is.client.dto.MemberDto;
import me.sup2is.web.JsonResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = MemberService.class)
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @MockBean
    MemberServiceClient memberServiceClient;

    @MockBean
    CachedMemberService cachedMemberService;

    @Test
    @DisplayName("멤버정보를 cachedMemberService 가져오기")
    public void get_member() {
        //given
        String email = "test@example";
        MemberDto memberDto = MemberDto.builder()
                .address("서울 강남")
                .name("choi")
                .password("qwer!23")
                .phone("010-3132-1089")
                .zipCode(12345)
                .enable(true)
                .authorities(Arrays.asList("MEMBER"))
                .email(email)
                .memberId(1L)
                .build();

        Mockito.when(cachedMemberService.findMember(email))
                .thenReturn(Optional.of(memberDto));

        //when
        MemberDto member = memberService.getMember(email);

        //then
        assertNotNull(member);

    }

    @Test
    @DisplayName("멤버정보를 memberServiceClient에서 가져오기")
    public void get_member_with_caching() {
        //given
        String email = "test@example";
        MemberDto memberDto = MemberDto.builder()
                .address("서울 강남")
                .name("choi")
                .password("qwer!23")
                .phone("010-3132-1089")
                .zipCode(12345)
                .enable(true)
                .authorities(Arrays.asList("MEMBER"))
                .email(email)
                .memberId(1L)
                .build();

        Mockito.when(cachedMemberService.findMember(email))
                .thenReturn(Optional.empty());

        Mockito.when(memberServiceClient.getMember(email))
                .thenReturn(memberDto);

        //when
        MemberDto member = memberService.getMember(email);

        //then
        assertNotNull(member);
        verify(cachedMemberService,times(1)).caching(member);

    }


}
package me.sup2is.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.client.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@SpringBootTest(classes = {CachedMemberService.class, ObjectMapper.class})
class CachedMemberServiceTest {

    @Autowired
    CachedMemberService cachedMemberService;

    @MockBean
    HashOperations<String, String, Object> hashOperations;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("redis에서 member정보 조회")
    public void find_member() {
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

        Mockito.when(hashOperations.get("member:" + email, "member"))
                .thenReturn(memberDto);


        //when
        Optional<MemberDto> member = cachedMemberService.findMember(email);

        //then
        assertTrue(member.isPresent());

    }

    @Test
    @DisplayName("redis에서 member정보 조회 실패")
    public void find_member_not_exist() {
        //given

        String email = "test@example";

        Mockito.when(hashOperations.get("member:" + email, "member"))
                .thenReturn(null);

        //when
        Optional<MemberDto> member = cachedMemberService.findMember(email);

        //then
        assertEquals(Optional.empty(), member);

    }
}
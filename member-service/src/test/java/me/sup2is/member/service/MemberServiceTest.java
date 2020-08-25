package me.sup2is.member.service;

import me.sup2is.member.domain.Member;
import me.sup2is.member.exception.MemberNotFoundException;
import me.sup2is.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    @InjectMocks
    MemberService memberService;

    @MockBean
    MemberRepository memberRepository;

    @Test
    public void find_one() {
        //given
        Member.Builder builder = new Member.Builder();
        Member member = Member.createMember(builder.address("서울시 강남구")
                                                    .email("dev.sup2is@gmail.com")
                                                    .name("sup2is")
                                                    .password("qwer!23")
                                                    .phone("010-3132-1089")
                                                    .zipCode("65482"));

        Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findOne(1L);

        //then
        assertEquals(member, findMember);
    }


    @Test
    public void find_one_not_exist() {
        //given
        Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.findOne(1L));
    }

}
package me.sup2is.member.repository;

import me.sup2is.member.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void find_by_email() {
        //given
        Member member = Member.Builder.builder()
                .address("서울시 강남구")
                .email("dev.sup2is@gmail.com")
                .name("sup2is")
                .password("qwer!23")
                .phone("010-3132-1089")
                .zipCode(65482)
                .authorities(Arrays.asList("MEMBER"))
                .build()
                .toEntity();

        memberRepository.save(member);

        //when
        Optional<Member> byEmail = memberRepository.findByEmail(member.getEmail());

        //then
        assertEquals(member, byEmail.get());
    }

}
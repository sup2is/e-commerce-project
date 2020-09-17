package me.sup2is.member.service;

import javassist.bytecode.DuplicateMemberException;
import me.sup2is.member.domain.Auth;
import me.sup2is.member.domain.Authority;
import me.sup2is.member.domain.Member;
import me.sup2is.member.repository.AuthorityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({MemberService.class, AuthorityService.class})
@Transactional
class MemberAuthorityServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    AuthorityService authorityService;

    @Autowired
    AuthorityRepository authorityRepository;

    @Test
    public void find_one() throws DuplicateMemberException {
        //given
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

        memberService.save(member);

        //when
        Member findMember = memberService.findOne(member.getId());
        Authority authority = authorityRepository.findById(member.getAuthorities().get(0).getId()).get();

        //then
        assertEquals(member, findMember);
        assertEquals(Auth.MEMBER, authority.getAuth());
    }

}
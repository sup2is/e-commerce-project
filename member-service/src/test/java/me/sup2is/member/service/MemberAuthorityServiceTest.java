package me.sup2is.member.service;

import me.sup2is.member.domain.Auth;
import me.sup2is.member.domain.Authority;
import me.sup2is.member.domain.Member;
import me.sup2is.member.exception.MemberNotFoundException;
import me.sup2is.member.repository.AuthorityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

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
    public void find_one() {
        //given
        Member.Builder builder = new Member.Builder();
        Member member = Member.createMember(builder.address("서울시 강남구")
                                                    .email("dev.sup2is@gmail.com")
                                                    .name("sup2is")
                                                    .password("qwer!23")
                                                    .phone("010-3132-1089")
                                                    .zipCode(65482));

        memberService.save(member);

        //when
        Member findMember = memberService.findOne(member.getId());
        Authority authority = authorityRepository.findById(member.getAuthorities().get(0).getId()).get();

        //then
        assertEquals(member, findMember);
        assertEquals(Auth.MEMBER, authority.getAuth());
    }

}
package me.sup2is.member.service;

import javassist.bytecode.DuplicateMemberException;
import me.sup2is.member.domain.Member;
import me.sup2is.member.domain.dto.ModifyMember;
import me.sup2is.member.exception.MemberNotFoundException;
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
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    public void find_one() throws DuplicateMemberException {
        //given
        Member member = getMember("서울시 강남구", "sup2is", "qwer!23", "010-3132-1089", 12345);

        memberService.save(member);

        //when
        Member findMember = memberService.findOne(member.getId());

        //then
        assertEquals(member, findMember);
    }


    @Test
    public void find_one_not_exist() {
        //given
        //when
        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.findOne(2L));
    }

    @Test
    public void save_duplicate_email() throws DuplicateMemberException {
        //given
        Member member = getMember("서울시 강남구", "sup2is", "qwer!23", "010-3132-1089", 12345);

        memberService.save(member);
        Member newMember = getMember("서울시 강남구2222", "sup2is222", "qwer!23222", "010-3132-1089", 12345);

        //when
        //then
        assertThrows(DuplicateMemberException.class, () -> memberService.save(newMember));
    }


    @Test
    public void modify() throws DuplicateMemberException {
        //given
        Member member = getMember("서울시 강남구", "sup2is", "qwer!23", "010-3132-1089", 12345);

        memberService.save(member);

        ModifyMember modifyMember
                = new ModifyMember("aaaaaaa123", "변경될 이름", "변경될 주소", 12345, "010-3132-1111");

        //when
        memberService.modify(member.getEmail(), modifyMember);

        //then
        Member findMember = memberService.findOne(member.getId());
        assertEquals(modifyMember.getAddress(), findMember.getAddress());
        assertEquals(modifyMember.getName(), findMember.getName());
        assertEquals(modifyMember.getPassword(), findMember.getPassword());
        assertEquals(modifyMember.getPhone(), findMember.getPhone());
        assertEquals(modifyMember.getZipCode(), findMember.getZipCode());

    }

    private Member getMember(String address, String sup2is, String password, String phone, int zipCode) {
        return Member.Builder.builder()
                .address(address)
                .email("dev.sup2is@gmail.com")
                .name(sup2is)
                .password(password)
                .phone(phone)
                .zipCode(zipCode)
                .authorities(Arrays.asList("MEMBER"))
                .build()
                .toEntity();
    }

}
package me.sup2is.member.config;

import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import me.sup2is.member.domain.Auth;
import me.sup2is.member.domain.Member;
import me.sup2is.member.service.MemberService;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Profile({"dev", "dev-integration"})
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final MemberService memberService;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws DuplicateMemberException {

        Member seller = Member.Builder.builder()
                .address("서울시 송파구")
                .authorities(Arrays.asList(Auth.SELLER.name()))
                .name("choi")
                .password("qwer!23")
                .phone("010-3132-1099")
                .zipCode(12345)
                .email("dev.sup2is@gmail.com")
                .enable(true)
                .build()
                .toEntity();

        memberService.save(seller);

        Member member = Member.Builder.builder()
                .address("서울시 강동구")
                .authorities(Arrays.asList(Auth.MEMBER.name()))
                .name("choi")
                .password("qwer!23")
                .phone("010-3132-1099")
                .zipCode(12345)
                .email("test@example.com")
                .enable(true)
                .build()
                .toEntity();

        memberService.save(member);
    }

}

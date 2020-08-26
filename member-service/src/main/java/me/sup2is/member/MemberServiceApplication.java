package me.sup2is.member;

import lombok.RequiredArgsConstructor;
import me.sup2is.member.domain.Member;
import me.sup2is.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class})
@RequiredArgsConstructor
public class MemberServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberServiceApplication.class, args);
    }

    @Autowired
    private MemberService memberService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Member.Builder builder = new Member.Builder();
            Member member = Member.createMember(builder.address("서울시 강남구")
                    .email("test@gmail.com")
                    .name("sup2is")
                    .password("qwer!23")
                    .phone("010-3132-1089")
                    .zipCode(65482));
            memberService.save(member);
        };
    }

}

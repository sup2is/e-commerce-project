package me.sup2is.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.client.MemberServiceClient;
import me.sup2is.client.dto.MemberDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public MemberDto authenticateByEmailAndPassword(String email, String password) {
        MemberDto memberDto = memberService.getMember(email);

        if(!passwordEncoder.matches(password, memberDto.getPassword())) {
            throw new BadCredentialsException("Password not matched");
        }
        return memberDto;
    }

}

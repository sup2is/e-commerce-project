package me.sup2is.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.client.MemberServiceClient;
import me.sup2is.client.dto.MemberClientDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberServiceClient memberServiceClient;
    private final PasswordEncoder passwordEncoder;

    public MemberClientDto authenticateByEmailAndPassword(String email, String password) {
        MemberClientDto memberClientDto = memberServiceClient.getMember(email).getData();
        if(!passwordEncoder.matches(password, memberClientDto.getPassword())) {
            throw new BadCredentialsException("Password not matched");
        }
        return memberClientDto;
    }

}

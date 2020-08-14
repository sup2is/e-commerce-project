package me.sup2is.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationGenerator {

    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticationResponseDto createJwtAuthenticationFromUserDetails(UserDetails userDetails) {
        return AuthenticationResponseDto.createAuthenticationFromTokens(
                jwtTokenUtil.generateToken(userDetails.getUsername(), JwtTokenType.AUTH),
                jwtTokenUtil.generateToken(userDetails.getUsername(), JwtTokenType.REFRESH)
        );
    }
}
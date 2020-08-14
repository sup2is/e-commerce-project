package me.sup2is.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationResponseDto {

    private String accessToken;
    private String refreshToken;

    public static AuthenticationResponseDto createAuthenticationFromTokens(String accessToken, String refreshToken) {
        AuthenticationResponseDto jwtAuthenticationDto = new AuthenticationResponseDto();
        jwtAuthenticationDto.accessToken = accessToken;
        jwtAuthenticationDto.refreshToken = refreshToken;
        return jwtAuthenticationDto;
    }

}
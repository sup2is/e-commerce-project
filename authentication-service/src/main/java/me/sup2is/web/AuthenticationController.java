package me.sup2is.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.client.dto.MemberDto;
import me.sup2is.dto.AuthenticationRequestDto;
import me.sup2is.dto.AuthenticationResponseDto;
import me.sup2is.jwt.JwtTokenType;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/token")
    public ResponseEntity<JsonResult<AuthenticationResponseDto>> generateJwtToken
            (@RequestBody AuthenticationRequestDto authenticationRequestDto) {
        MemberDto memberDto = authenticationService.authenticateByEmailAndPassword(
                authenticationRequestDto.getUsername()
                , authenticationRequestDto.getPassword());
        AuthenticationResponseDto authenticationFromTokens = AuthenticationResponseDto.createAuthenticationFromTokens(
                jwtTokenUtil.generateToken(memberDto.getEmail(), JwtTokenType.AUTH),
                jwtTokenUtil.generateToken(memberDto.getEmail(), JwtTokenType.REFRESH));
        return ResponseEntity.ok(new JsonResult<>(authenticationFromTokens));
    }
}
